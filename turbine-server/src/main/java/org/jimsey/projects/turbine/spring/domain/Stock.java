/**
 * The MIT License
 * Copyright (c) 2015 the-james-burton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jimsey.projects.turbine.spring.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.camel.components.SpringSimpleMessagingConstants;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.camel.routes.StrategyRoute;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.jimsey.projects.turbine.spring.domain.indicators.BollingerBands;
import org.jimsey.projects.turbine.spring.domain.indicators.TurbineIndicator;
import org.jimsey.projects.turbine.spring.domain.strategies.SMAStrategy;
import org.jimsey.projects.turbine.spring.domain.strategies.TurbineStrategy;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

@ConfigurationProperties(prefix = "consumer")
public class Stock {

  private static final Logger logger = LoggerFactory.getLogger(Stock.class);

  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  @Autowired
  @NotNull
  private CamelContext camel;

  private ProducerTemplate producer;

  private String symbol;

  private String market;

  private TickJson tick;

  private StockJson stock;

  private final TimeSeries series = new TimeSeries(new ArrayList<Tick>());

  private final ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private final List<TurbineIndicator> turbineIndicators = new ArrayList<>();

  private final List<TurbineStrategy> turbineStrategies = new ArrayList<>();

  private final Map<String, Double> indicators = new HashMap<>();

  public Stock(final String market, final String symbol) {
    this.market = market;
    this.symbol = symbol;
    // TODO better way to initialize indicators..?
    turbineIndicators.add(new BollingerBands(series, closePriceIndicator));
    turbineStrategies.add(new SMAStrategy(series, closePriceIndicator));
  }

  @PostConstruct
  public void init() {
    producer = camel.createProducerTemplate();
  }

  public void receiveTick(TickJson tick) {
    this.tick = tick;
    logger.debug("market: {}, symbol: {}, receiveTick: {}", market, symbol, tick.getTimestamp());
    series.addTick(tick);
    turbineIndicators.stream().forEach((indicator) -> {
      indicator.update();
      indicators.putAll(indicator.getValues());
    });
    turbineStrategies.stream().forEach((strategy) -> {
      publishStrategy(strategy.run(tick));
    });

    createStock();
  }

  private void publishStrategy(StrategyJson strategyJson) {

    if (strategyJson == null) {
      return;
    }

    Map<String, Object> headers = new HashMap<String, Object>();

    headers.put(TurbineConstants.HEADER_FOR_OBJECT_TYPE, strategyJson.getClass().getName());
    headers.put(SpringSimpleMessagingConstants.DESTINATION_SUFFIX,
        String.format(".%s.%s", strategyJson.getMarket(), strategyJson.getSymbol()));

    logger.info(" *** STRATEGY : producing: [body: {}, headers: {}]", strategyJson.toString(), new JSONObject(headers));

    String text = camel.getTypeConverter().convertTo(String.class, strategyJson);
    producer.sendBodyAndHeaders(StrategyRoute.STRATEGY_PUBLISH, text, headers);

  }

  private void createStock() {
    Double close = closePriceIndicator.getValue(series.getEnd()).toDouble();
    // TODO refactor StockJson to take a map instead of explict values...
    stock = new StockJson(tick.getDate(), close, indicators, symbol, market, tick.getTimestamp());
  }

  public StockJson getStock() {
    return stock;
  }
}

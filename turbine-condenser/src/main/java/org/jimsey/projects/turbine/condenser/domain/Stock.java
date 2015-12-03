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
package org.jimsey.projects.turbine.condenser.domain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.domain.indicators.BollingerBands;
import org.jimsey.projects.turbine.condenser.domain.indicators.SMA12;
import org.jimsey.projects.turbine.condenser.domain.indicators.TurbineIndicator;
import org.jimsey.projects.turbine.condenser.domain.strategies.CCICorrectionStrategy;
import org.jimsey.projects.turbine.condenser.domain.strategies.SMAStrategy;
import org.jimsey.projects.turbine.condenser.domain.strategies.TurbineStrategy;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
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

  private String symbol;

  private String market;

  private final TimeSeries series = new TimeSeries(new ArrayList<Tick>());

  private final ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private final List<TurbineIndicator> turbineIndicators = new ArrayList<>();

  private final List<TurbineStrategy> turbineStrategies = new ArrayList<>();

  public Stock(final String market, final String symbol) {
    this.market = market;
    this.symbol = symbol;

    // TODO eventually we should only add indicators and strategies when a user requests them...
    turbineIndicators.add(new SMA12(series, closePriceIndicator));
    turbineIndicators.add(new BollingerBands(series, closePriceIndicator));

    turbineStrategies.add(new CCICorrectionStrategy(series, closePriceIndicator));
    turbineStrategies.add(new SMAStrategy(series, closePriceIndicator));
  }

  @PostConstruct
  public void init() {
  }

  public void receiveTick(TickJson tick) {
    logger.debug("market: {}, symbol: {}, receiveTick: {}", market, symbol, tick.getTimestamp());
    series.addTick(tick);
  }

  public List<TurbineIndicator> getIndicators() {
    return turbineIndicators;
  }

  public List<TurbineStrategy> getStrategies() {
    return turbineStrategies;
  }

}

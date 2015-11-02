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

import org.jimsey.projects.turbine.spring.domain.indicators.BollingerBands;
import org.jimsey.projects.turbine.spring.domain.indicators.TurbineIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

public class Stock {

  private static final Logger logger = LoggerFactory.getLogger(Stock.class);

  private String symbol;

  private String market;

  private TickJson tick;

  private StockJson stock;

  private final TimeSeries series = new TimeSeries(new ArrayList<Tick>());

  private final ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private final List<TurbineIndicator> turbineIndicators = new ArrayList<>();

  private final Map<String, Double> indicators = new HashMap<>();

  public Stock(final String market, final String symbol) {
    this.market = market;
    this.symbol = symbol;
    // TODO better way to initialize indicators..?
    turbineIndicators.add(new BollingerBands(series, closePriceIndicator));
  }

  public void receiveTick(TickJson tick) {
    this.tick = tick;
    logger.debug("market: {}, symbol: {}, receiveTick: {}", market, symbol, tick.getTimestamp());
    series.addTick(tick);
    turbineIndicators.stream().forEach((indicator) -> {
      indicator.update();
      indicators.putAll(indicator.getValues());
    });
    createStock();
  }

  private void createStock() {
    Double cpi = closePriceIndicator.getValue(series.getEnd()).toDouble();
    // TODO refactor StockJson to take a map instead of explict values...
    stock = new StockJson(tick.getDate(), cpi, indicators, symbol, market, tick.getTimestamp());
  }

  public StockJson getStock() {
    return stock;
  }
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsUpperIndicator;

public class Stock {

  private static final Logger logger = LoggerFactory.getLogger(Stock.class);

  private final int timeFrame = 10;

  private String symbol;

  private String market;

  private TickJson tick;

  private StockJson stock;

  private final TimeSeries series = new TimeSeries(new ArrayList<Tick>());

  private final ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private final SMAIndicator smaIndicator = new SMAIndicator(closePriceIndicator, timeFrame);

  private final StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(smaIndicator, timeFrame);

  private final BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(
      smaIndicator);

  private final BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(
      bollingerBandsMiddleIndicator, standardDeviationIndicator);

  private final BollingerBandsUpperIndicator bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(
      bollingerBandsMiddleIndicator, standardDeviationIndicator);

  public Stock(final String market, final String symbol) {
    this.market = market;
    this.symbol = symbol;
  }

  public void receiveTick(TickJson tick) {
    this.tick = tick;
    logger.debug("market: {}, symbol: {}, receiveTick: {}", market, symbol, tick.getTimestamp());
    series.addTick(tick);
    createStock();
  }

  private void createStock() {
    Double cpi = closePriceIndicator.getValue(series.getEnd()).toDouble();
    Double bbmi = bollingerBandsMiddleIndicator.getValue(series.getEnd()).toDouble();
    Double bbli = bollingerBandsLowerIndicator.getValue(series.getEnd()).toDouble();
    Double bbui = bollingerBandsUpperIndicator.getValue(series.getEnd()).toDouble();
    stock = new StockJson(tick.getDate(), cpi, bbmi, bbli, bbui, symbol, market, tick.getTimestamp());
  }

  public StockJson getStock() {
    return stock;
  }
}

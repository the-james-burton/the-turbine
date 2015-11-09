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
package org.jimsey.projects.turbine.spring.domain.strategies;

import org.jimsey.projects.turbine.spring.domain.StrategyJson;
import org.jimsey.projects.turbine.spring.domain.TickJson;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.trading.rules.OverIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.UnderIndicatorRule;

public class SMAStrategy implements TurbineStrategy {

  private final String name = "SMAStrategy";

  private final TimeSeries series;

  private final ClosePriceIndicator closePriceIndicator;

  private final SMAIndicator sma;

  private final Strategy strategy;

  private final TradingRecord tradingRecord = new TradingRecord();

  // TODO configure trade size...
  private final int tradeSizeInt = 1;

  private int position = 0;

  private double cost = 0.0d;

  private double value = 0.0d;

  public SMAStrategy(final TimeSeries series, final ClosePriceIndicator closePriceIndicator) {
    this.series = series;
    this.closePriceIndicator = closePriceIndicator;

    // setup this strategy...
    sma = new SMAIndicator(closePriceIndicator, 12);
    strategy = new Strategy(
        new OverIndicatorRule(sma, closePriceIndicator),
        new UnderIndicatorRule(sma, closePriceIndicator));
  }

  @Override
  public StrategyJson run(TickJson tick) {
    int index = series.getEnd();
    double close = closePriceIndicator.getValue(index).toDouble();
    boolean shouldEnter = strategy.shouldEnter(index, tradingRecord);
    boolean shouldExit = strategy.shouldExit(index, tradingRecord);
    StrategyJson result = null;
    if (shouldEnter) {
      position += tradeSizeInt;
      cost += close * tradeSizeInt;
      value = close * position;
      tradingRecord.enter(index, Decimal.valueOf(close), Decimal.valueOf(tradeSizeInt));
      result = new StrategyJson(
          tick.getDate(),
          tick.getSymbol(),
          tick.getMarket(),
          tick.getClose(),
          name, "enter", tradeSizeInt, position, cost, value,
          tick.getTimestamp());
    }
    if (shouldExit) {
      position -= tradeSizeInt;
      cost -= close * tradeSizeInt;
      value = close * position;
      tradingRecord.exit(index, Decimal.valueOf(close), Decimal.valueOf(-tradeSizeInt));
      result = new StrategyJson(
          tick.getDate(),
          tick.getSymbol(),
          tick.getMarket(),
          tick.getClose(),
          name, "exit", tradeSizeInt, position, cost, value,
          tick.getTimestamp());
    }
    return result;
  }

  // ---------------------------------
  @Override
  public Strategy getStrategy() {
    return strategy;
  }

  public int getPosition() {
    return position;
  }

  public double getCost() {
    return cost;
  }

  public double getValue() {
    return value;
  }
}

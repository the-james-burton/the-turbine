/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
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
package org.jimsey.projects.turbine.condenser.domain.strategies;

import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

public abstract class BaseStrategy implements TurbineStrategy {

  private final String name;

  private final TimeSeries series;

  protected final ClosePriceIndicator closePriceIndicator;

  protected Strategy strategy;

  private final TradingRecord tradingRecord = new TradingRecord();

  // TODO how to handle trade size...?
  private final int tradeSize = 1;

  private int position = 0;

  private double cash = 250.0d;

  private double value = 0.0d;

  public BaseStrategy(
      final TimeSeries series,
      final String name,
      final ClosePriceIndicator closePriceIndicator) {
    this.series = series;
    this.name = name;
    this.closePriceIndicator = closePriceIndicator;
  }

  @Override
  public StrategyJson run(TickJson tick) {
    int index = series.getEnd();
    // TODO check for empty series...
    double close = series.getLastTick().getClosePrice().toDouble();
    boolean shouldEnter = strategy.shouldEnter(index, tradingRecord);
    boolean shouldExit = strategy.shouldExit(index, tradingRecord);
    String action = TurbineCondenserConstants.ACTION_NONE;
    int thisTradeSize = tradeSize;
    if (shouldEnter && cash > (close * tradeSize)) {
      position += tradeSize;
      cash -= close * tradeSize;
      tradingRecord.enter(index, Decimal.valueOf(close), Decimal.valueOf(thisTradeSize));
      action = TurbineCondenserConstants.ACTION_ENTER;
    }
    // TODO handle negative (short) positions..?
    if (shouldExit && position > 0) {
      position -= tradeSize;
      cash += close * tradeSize;
      thisTradeSize = -tradeSize;
      tradingRecord.exit(index, Decimal.valueOf(close), Decimal.valueOf(thisTradeSize));
      action = TurbineCondenserConstants.ACTION_EXIT;
    }
    value = close * position;

    // by returning null, we can suppress non-operating strategy output if we wish...
    // the problem then is how to produce a decent visualisation in the UI...
    StrategyJson result = null;
    // if (action != TurbineCondenserConstants.ACTION_NONE) {
      result = new StrategyJson(
          tick.getTimestampAsObject(),
          tick.getTickerAsObject(),
          tick.getClose(),
          name, action, thisTradeSize, position, cash, value,
          tick.getTimestamp());
    // }
    
    // sadly, we can't use Optional here because of this compiler error...
    // "The method createMessage(Optional<Entity>, Map<String,Object>) in the type BaseSplitter
    // is not applicable for the arguments (Optional<StrategyJson>, Map<String,Object>)
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

  public double getCash() {
    return cash;
  }

  public double getValue() {
    return value;
  }

}

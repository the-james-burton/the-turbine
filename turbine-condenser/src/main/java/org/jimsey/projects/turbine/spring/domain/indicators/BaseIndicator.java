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
package org.jimsey.projects.turbine.spring.domain.indicators;

import java.util.Map;

import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

public abstract class BaseIndicator implements TurbineIndicator {

  protected final int timeFrame;

  protected final TimeSeries series;

  private final String name;

  protected final ClosePriceIndicator closePriceIndicator;

  public BaseIndicator(
      final int timeframe,
      final TimeSeries series,
      final String name,
      final ClosePriceIndicator closePriceIndicator) {
    this.timeFrame = timeframe;
    this.series = series;
    this.name = name;
    this.closePriceIndicator = closePriceIndicator;
  }

  protected abstract Map<String, Double> computeValues();

  public IndicatorJson run(TickJson tick) {
    return new IndicatorJson(
        tick.getTimestampAsObject(),
        tick.getClose(),
        computeValues(),
        tick.getSymbol(),
        tick.getMarket(),
        name,
        tick.getTimestamp());
  };

}

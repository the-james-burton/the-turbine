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
package org.jimsey.projects.turbine.condenser.domain.indicators;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

public abstract class BaseIndicator implements TurbineIndicator {

  protected static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected final IndicatorInstance instance;

  protected final TimeSeries series;

  protected final ClosePriceIndicator closePriceIndicator;

  /** most indicators have just one indicator... **/
  protected CachedIndicator<Decimal> indicator;

  // sacrifice finals for redundant subclass constructor...
  // public BaseIndicator() {
  // }

  public BaseIndicator(
      final IndicatorInstance instance,
      final TimeSeries series,
      final ClosePriceIndicator closePriceIndicator) {
    this.instance = instance;
    this.series = series;
    this.closePriceIndicator = closePriceIndicator;
    init();
  }

  /** @return the calculation results - default implementation useful in most cases... */
  protected Map<String, Double> computeValues() {
    logger.debug("computeValues:{}", instance.getName());
    Map<String, Double> values = new HashMap<>();
    double value = 0;
    try {
      value = indicator.getValue(series.getEnd()).toDouble();
    } catch (Exception e) {
      logger.warn("computeValues had a problem:{}, {}",
          instance.getName(), e.getMessage());
    }
    values.put(instance.getName(), value);
    return values;
  }

  /** setup this indicator as required */
  protected abstract void init();

  /** to be used by the implementing classes */
  protected void setIndicator(CachedIndicator<Decimal> indicator) {
    this.indicator = indicator;
  }

  /** to be used by the implementing classes to verify setup */
  protected void validateNone() {
    Validate.validState(instance.getTimeframe1() == null, "%s: timeframe1 must be null", instance.getName());
    Validate.validState(instance.getTimeframe2() == null, "%s: timeframe2 must be null", instance.getName());
    Validate.validState(instance.getTimeframe3() == null, "%s: timeframe3 must be null", instance.getName());
  }

  /** to be used by the implementing classes to verify setup */
  protected void validateOne() {
    Validate.notNull(instance.getTimeframe1(), "%s: timeframe1 must not be null", instance.getName());
    Validate.validState(instance.getTimeframe2() == null, "%s: timeframe2 must be null", instance.getName());
    Validate.validState(instance.getTimeframe3() == null, "%s: timeframe3 must be null", instance.getName());
  }

  /** to be used by the implementing classes to verify setup */
  protected void validateTwo() {
    Validate.notNull(instance.getTimeframe1(), "%s: timeframe1 must not be null", instance.getName());
    Validate.notNull(instance.getTimeframe2(), "%s: timeframe2 must not be null", instance.getName());
    Validate.validState(instance.getTimeframe3() == null, "%s: timeframe3 must be null", instance.getName());
  }

  protected void validateThree() {
    Validate.notNull(instance.getTimeframe1(), "%s: timeframe1 must not be null", instance.getName());
    Validate.notNull(instance.getTimeframe2(), "%s: timeframe2 must not be null", instance.getName());
    Validate.notNull(instance.getTimeframe3(), "%s: timeframe3 must not be null", instance.getName());
  }

  // private static AtomicInteger indicatorCount = new AtomicInteger();

  public IndicatorJson run(final TickJson tick) {

    // wait for the series to be populated one the first tick if need be...
    // this is a pragmatic rather than elegant solution
    // something with a future or a latch might be better
    // or even letting the first
    // while (series == null || series.getTickCount() < 1) {
    // logger.info("waiting for empty series : {}", tick.getTicker());
    // Try.run(() -> TimeUnit.MILLISECONDS.sleep(100));
    // }

    // this count is 16000 when parallel... nothing lost here!
    // logger.info(" *-> indicatorCount:{}", indicatorCount.incrementAndGet());

    return new IndicatorJson(
        tick.getTimestampAsObject(),
        tick.getClose(),
        computeValues(),
        tick.getTickerAsObject(),
        instance.getName(),
        tick.getTimestamp());
  }

  public IndicatorInstance getInstance() {
    return instance;
  }

}

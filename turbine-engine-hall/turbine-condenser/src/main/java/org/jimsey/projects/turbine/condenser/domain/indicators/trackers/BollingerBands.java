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
package org.jimsey.projects.turbine.condenser.domain.indicators.trackers;

import java.util.HashMap;
import java.util.Map;

import org.jimsey.projects.turbine.condenser.domain.indicators.BaseIndicator;
import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorInstance;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;

public class BollingerBands extends BaseIndicator {

  private SMAIndicator smaIndicator;
  private StandardDeviationIndicator standardDeviationIndicator;
  private BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator;
  private BollingerBandsLowerIndicator bollingerBandsLowerIndicator;
  private BollingerBandsUpperIndicator bollingerBandsUpperIndicator;

  public BollingerBands(IndicatorInstance instance, TimeSeries series, ClosePriceIndicator closePriceIndicator) {
    super(instance, series, closePriceIndicator);
  }

  @Override
  protected void init() {
    validateTwo();
    smaIndicator = new SMAIndicator(closePriceIndicator, instance.getTimeframe1());
    standardDeviationIndicator = new StandardDeviationIndicator(smaIndicator, instance.getTimeframe1());
    bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(
        smaIndicator);
    bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(
        bollingerBandsMiddleIndicator, standardDeviationIndicator, Decimal.valueOf(instance.getTimeframe2()));
    bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(
        bollingerBandsMiddleIndicator, standardDeviationIndicator, Decimal.valueOf(instance.getTimeframe2()));

  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put(instance.generateName("bollingerBandsLowerIndicator"),
        bollingerBandsLowerIndicator.getValue(series.getEnd()).toDouble());
    values.put(instance.generateName("bollingerBandsUpperIndicator"),
        bollingerBandsUpperIndicator.getValue(series.getEnd()).toDouble());
    return values;
  }

}

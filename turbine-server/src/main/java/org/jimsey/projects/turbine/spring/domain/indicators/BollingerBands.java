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

import java.util.HashMap;
import java.util.Map;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsUpperIndicator;

public class BollingerBands extends BaseIndicator {

  private final SMAIndicator smaIndicator;

  private final StandardDeviationIndicator standardDeviationIndicator;

  private final BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator;

  private final BollingerBandsLowerIndicator bollingerBandsLowerIndicator;

  private final BollingerBandsUpperIndicator bollingerBandsUpperIndicator;

  public BollingerBands(final TimeSeries series, final ClosePriceIndicator indicator) {
    super(10, series, indicator);

    // setup this indicator...
    smaIndicator = new SMAIndicator(closePriceIndicator, timeFrame);
    standardDeviationIndicator = new StandardDeviationIndicator(smaIndicator, timeFrame);
    bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(
        smaIndicator);
    bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(
        bollingerBandsMiddleIndicator, standardDeviationIndicator);
    bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(
        bollingerBandsMiddleIndicator, standardDeviationIndicator);

  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put("bollingerBandsLowerIndicator", bollingerBandsLowerIndicator.getValue(series.getEnd()).toDouble());
    values.put("bollingerBandsUpperIndicator", bollingerBandsUpperIndicator.getValue(series.getEnd()).toDouble());
    return values;
  }

}

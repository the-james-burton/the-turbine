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

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.keltner.KeltnerChannelLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.keltner.KeltnerChannelMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.keltner.KeltnerChannelUpperIndicator;

@EnableTurbineIndicator(name = "KeltnerChannels", isOverlay = true)
public class KeltnerChannels extends BaseIndicator {

  private final KeltnerChannelLowerIndicator keltnerChannelLower;
  private final KeltnerChannelMiddleIndicator keltnerChannelMiddle;
  private final KeltnerChannelUpperIndicator keltnerChannelUpper;

  public KeltnerChannels(final TimeSeries series, final ClosePriceIndicator indicator) {
    super(20, series, MethodHandles.lookup().lookupClass().getSimpleName(), indicator);

    // setup this indicator...
    keltnerChannelMiddle = new KeltnerChannelMiddleIndicator(series, timeFrame);
    keltnerChannelUpper = new KeltnerChannelUpperIndicator(keltnerChannelMiddle, Decimal.TWO, timeFrame);
    keltnerChannelLower = new KeltnerChannelLowerIndicator(keltnerChannelMiddle, Decimal.TWO, timeFrame);
  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put("keltnerChannelLower", keltnerChannelLower.getValue(series.getEnd()).toDouble());
    values.put("keltnerChannelMiddle", keltnerChannelMiddle.getValue(series.getEnd()).toDouble());
    values.put("keltnerChannelUpper", keltnerChannelUpper.getValue(series.getEnd()).toDouble());
    return values;
  }

}

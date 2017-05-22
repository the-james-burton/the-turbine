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

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ChandelierExitLongIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ChandelierExitShortIndicator;

/**
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:aroon_oscillator
 *
 * @author the-james-burton
 */
@EnableTurbineIndicator(name = "Aroon", isOverlay = true)
public class ChandelierExit extends BaseIndicator {

  private final ChandelierExitLongIndicator chandelierExitLong;

  private final ChandelierExitShortIndicator chandelierExitShort;

  public ChandelierExit(final TimeSeries series, final ClosePriceIndicator indicator) {
    super(22, series, MethodHandles.lookup().lookupClass().getSimpleName(), indicator);

    // setup this indicator...
    chandelierExitLong = new ChandelierExitLongIndicator(series);
    chandelierExitShort = new ChandelierExitShortIndicator(series);
  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put("chandelierExitLong", chandelierExitLong.getValue(series.getEnd()).toDouble());
    values.put("chandelierExitShort", chandelierExitShort.getValue(series.getEnd()).toDouble());
    return values;
  }

}

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
package org.jimsey.projects.turbine.condenser.domain.indicators;

import java.util.HashMap;
import java.util.Map;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.AroonDownIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.AroonUpIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

/**
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:aroon_oscillator
 *
 * @author the-james-burton
 */
@EnableTurbineIndicator(name = "AroonOscillator", isOverlay = false)
public class AroonOscillator extends BaseIndicator {

  private final AroonUpIndicator aroonUpIndicator;

  private final AroonDownIndicator aroonDownIndicator;

  public AroonOscillator(final TimeSeries series, final ClosePriceIndicator indicator) {
    super(25, series, AroonOscillator.class.getSimpleName(), indicator);

    // setup this indicator...
    aroonUpIndicator = new AroonUpIndicator(series, timeFrame);
    aroonDownIndicator = new AroonDownIndicator(series, timeFrame);
  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    double aroonUp = aroonUpIndicator.getValue(series.getEnd()).toDouble();
    double aroonDown = aroonDownIndicator.getValue(series.getEnd()).toDouble();
    values.put("aroonOscillator", aroonUp - aroonDown);
    return values;
  }

}

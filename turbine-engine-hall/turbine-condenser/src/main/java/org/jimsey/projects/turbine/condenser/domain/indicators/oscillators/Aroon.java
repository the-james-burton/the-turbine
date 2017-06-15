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
package org.jimsey.projects.turbine.condenser.domain.indicators.oscillators;

import java.util.HashMap;
import java.util.Map;

import org.jimsey.projects.turbine.condenser.domain.indicators.BaseIndicator;
import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorInstance;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.AroonDownIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.AroonUpIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;

/**
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:aroon_oscillator
 *
 * @author the-james-burton
 */
public class Aroon extends BaseIndicator {

  private AroonUpIndicator aroonUp;

  private AroonDownIndicator aroonDown;

  public Aroon(IndicatorInstance instance, TimeSeries series, ClosePriceIndicator closePriceIndicator) {
    super(instance, series, closePriceIndicator);
  }

  @Override
  protected void init() {
    validateOne();
    aroonUp = new AroonUpIndicator(series, instance.getTimeframe1());
    aroonDown = new AroonDownIndicator(series, instance.getTimeframe1());
  }

  /** not using the default implementation */
  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    double up = aroonUp.getValue(series.getEnd()).toDouble();
    double down = aroonDown.getValue(series.getEnd()).toDouble();
    values.put(instance.getName(), up - down);
    return values;
  }

}

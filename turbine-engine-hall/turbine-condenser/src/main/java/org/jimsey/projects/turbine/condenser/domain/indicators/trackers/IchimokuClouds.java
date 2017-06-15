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

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuChikouSpanIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuKijunSenIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuSenkouSpanAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuSenkouSpanBIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuTenkanSenIndicator;

public class IchimokuClouds extends BaseIndicator {

  private IchimokuChikouSpanIndicator ichimokuChikouSpan;
  private IchimokuKijunSenIndicator ichimokuKijunSen;
  private IchimokuSenkouSpanAIndicator ichimokuSenkouSpanA;
  private IchimokuSenkouSpanBIndicator ichimokuSenkouSpanB;
  private IchimokuTenkanSenIndicator ichimokuTenkanSen;

  public IchimokuClouds(IndicatorInstance instance, TimeSeries series, ClosePriceIndicator closePriceIndicator) {
    super(instance, series, closePriceIndicator);
  }

  @Override
  protected void init() {
    validateThree();

    // NOTE that this indicator suffers a bug in AbstractIchimokuLineIndicator.calculate()
    // it is already fixed in the repo, so will be fixed when Ta4J is next released

    ichimokuTenkanSen = new IchimokuTenkanSenIndicator(series, instance.getTimeframe1()); // 9
    ichimokuKijunSen = new IchimokuKijunSenIndicator(series, instance.getTimeframe2()); // 26
    ichimokuChikouSpan = new IchimokuChikouSpanIndicator(series);
    ichimokuSenkouSpanA = new IchimokuSenkouSpanAIndicator(series, ichimokuTenkanSen, ichimokuKijunSen);
    ichimokuSenkouSpanB = new IchimokuSenkouSpanBIndicator(series, instance.getTimeframe3()); // 56
  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put(instance.generateName("ichimokuChikouSpan"), ichimokuChikouSpan.getValue(series.getEnd()).toDouble());
    values.put(instance.generateName("ichimokuKijunSen"), ichimokuKijunSen.getValue(series.getEnd()).toDouble());
    values.put(instance.generateName("ichimokuTenkanSen"), ichimokuTenkanSen.getValue(series.getEnd()).toDouble());
    values.put(instance.generateName("ichimokuSenkouSpanA"), ichimokuSenkouSpanA.getValue(series.getEnd()).toDouble());
    values.put(instance.generateName("ichimokuSenkouSpanB"), ichimokuSenkouSpanB.getValue(series.getEnd()).toDouble());
    return values;
  }

}

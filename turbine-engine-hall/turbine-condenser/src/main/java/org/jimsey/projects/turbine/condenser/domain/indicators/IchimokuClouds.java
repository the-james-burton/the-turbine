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

import java.util.HashMap;
import java.util.Map;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuChikouSpanIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuKijunSenIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuSenkouSpanAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuSenkouSpanBIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ichimoku.IchimokuTenkanSenIndicator;

@EnableTurbineIndicator(name = "IchimokuClouds", isOverlay = true)
public class IchimokuClouds extends BaseIndicator {

  private final IchimokuChikouSpanIndicator ichimokuChikouSpanIndicator;

  private final IchimokuKijunSenIndicator ichimokuKijunSenIndicator;

  private final IchimokuSenkouSpanAIndicator ichimokuSenkouSpanAIndicator;

  private final IchimokuSenkouSpanBIndicator ichimokuSenkouSpanBIndicator;

  private final IchimokuTenkanSenIndicator ichimokuTenkanSenIndicator;

  public IchimokuClouds(final TimeSeries series, final ClosePriceIndicator indicator) {
    super(10, series, IchimokuClouds.class.getSimpleName(), indicator);

    // NOTE that this indicator suffers a bug in AbstractIchimokuLineIndicator.calculate()
    // it is already fixed in the repo, so will be fixed when Ta4J is next released

    // setup this indicator...
    ichimokuKijunSenIndicator = new IchimokuKijunSenIndicator(series);
    ichimokuChikouSpanIndicator = new IchimokuChikouSpanIndicator(series);
    ichimokuTenkanSenIndicator = new IchimokuTenkanSenIndicator(series);
    ichimokuSenkouSpanAIndicator = new IchimokuSenkouSpanAIndicator(series, ichimokuTenkanSenIndicator, ichimokuKijunSenIndicator);
    ichimokuSenkouSpanBIndicator = new IchimokuSenkouSpanBIndicator(series);
  }

  @Override
  public Map<String, Double> computeValues() {
    Map<String, Double> values = new HashMap<>();
    values.put("ichimokuChikouSpanIndicator", ichimokuChikouSpanIndicator.getValue(series.getEnd()).toDouble());
    values.put("ichimokuKijunSenIndicator", ichimokuKijunSenIndicator.getValue(series.getEnd()).toDouble());
    values.put("ichimokuTenkanSenIndicator", ichimokuTenkanSenIndicator.getValue(series.getEnd()).toDouble());
    values.put("ichimokuSenkouSpanAIndicator", ichimokuSenkouSpanAIndicator.getValue(series.getEnd()).toDouble());
    values.put("ichimokuSenkouSpanBIndicator", ichimokuSenkouSpanBIndicator.getValue(series.getEnd()).toDouble());
    return values;
  }

}

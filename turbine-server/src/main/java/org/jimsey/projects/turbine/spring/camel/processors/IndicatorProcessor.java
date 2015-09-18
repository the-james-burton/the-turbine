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
package org.jimsey.projects.turbine.spring.camel.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.jimsey.projects.turbine.spring.domain.TickJson;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsUpperIndicator;

public class IndicatorProcessor implements Processor {

  private List<Tick> ticks = new ArrayList<Tick>();

  private TimeSeries series = new TimeSeries(ticks);

  private ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(closePriceIndicator);
  private BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, closePriceIndicator);
  private BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, closePriceIndicator);

  @Override
  public void process(Exchange exchange) throws Exception {
    Message message = exchange.getIn();
    TickJson tick = message.getMandatoryBody(TickJson.class);
    series.addTick(tick);
    bbm.getValue(series.getEnd());
  }

}

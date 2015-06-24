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
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;

@Component
public class TickProcessor implements Processor {

  private static final Logger logger = LoggerFactory.getLogger(TickProcessor.class);

  private List<Tick> ticks = new ArrayList<Tick>();
  private TimeSeries series = new TimeSeries(ticks);
  private ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
  private SMAIndicator sma = new SMAIndicator(closePriceIndicator, 5);

  @Override
  public void process(final Exchange exchange) throws Exception {
    Message message = exchange.getIn();
    TickJson tick = message.getMandatoryBody(TickJson.class);
    String type = message.getHeader(TurbineConstants.HEADER_FOR_OBJECT_TYPE, String.class);
    series.addTick(tick);
    logger.info(tick.toString());
    logger.info("sma={}", sma.getValue(series.getEnd()));
  }

}

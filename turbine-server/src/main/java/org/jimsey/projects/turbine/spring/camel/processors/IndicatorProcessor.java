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

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.jimsey.projects.turbine.spring.domain.Symbol;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.domain.Xchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndicatorProcessor implements Processor {

  private static final Logger logger = LoggerFactory.getLogger(IndicatorProcessor.class);

  // TODO need a better implementation of some sort..?
  private Map<String, Xchange> exchanges = new HashMap<>();

  @Override
  public void process(Exchange exchange) throws Exception {
    Message message = exchange.getIn();
    TickJson tick = message.getMandatoryBody(TickJson.class);
    logger.info(tick.toString());
    Xchange xchange = exchanges.get(tick.getExchange());
    Symbol symbol = xchange.getSymbol(tick.getSymbol());
    logger.info("cpi:{}, bbu: {}, bbl: {}, bbm: {}",
        symbol.getClosePriceIndicator(),
        symbol.getBollingerBandsUpperIndicator(),
        symbol.getBollingerBandsLowerIndicator(),
        symbol.getBollingerBandsMiddleIndicator());
  }

}

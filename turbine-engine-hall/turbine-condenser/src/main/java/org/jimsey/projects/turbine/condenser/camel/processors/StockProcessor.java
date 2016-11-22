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
package org.jimsey.projects.turbine.condenser.camel.processors;

import javax.validation.constraints.NotNull;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.jimsey.projects.turbine.condenser.component.MarketsManager;
import org.jimsey.projects.turbine.condenser.domain.Market;
import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockProcessor implements Processor {

  private static final Logger logger = LoggerFactory.getLogger(StockProcessor.class);

  @Autowired
  @NotNull
  private MarketsManager marketsManager;

  @Override
  public void process(Exchange exchange) throws Exception {
    Message message = exchange.getIn();
    TickJson tick = message.getMandatoryBody(TickJson.class);
    logger.info(tick.toString());
    Market market = marketsManager.findMarket(tick.getMarket());
    Stock stock = market.findSymbol(tick.getSymbol());
    stock.receiveTick(tick);
    logger.info("stock: {}", stock.toString());
  }

}

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

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TickPublisher {

  private static final Logger logger = LoggerFactory.getLogger(TickPublisher.class);

  @Autowired
  @NotNull
  private SimpMessagingTemplate websockets;

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  // annotation is for receiving
  // @MessageMapping("topic.{exchange}.{symbol}")
  public void publish(TickJson tick) {
    // series.addTick(tick);
    // TODO send symbols on their own topic...
    //String topic = String.format("%s.%s.%s", infrastructureProperties.getWebsocketTicks(), tick.getExchange(), tick.getSymbol());
    String topic = String.format("%s", infrastructureProperties.getWebsocketTicks());
    if ("ABC".equals(tick.getSymbol())) {
      websockets.convertAndSend(topic, tick);
      // websockets.convertAndSend(tick);
      logger.info(String.format("websocket send: %s : %s", topic, tick.toString()));
    }

  }

}

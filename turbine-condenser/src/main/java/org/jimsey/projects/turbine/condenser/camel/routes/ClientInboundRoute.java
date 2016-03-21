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
package org.jimsey.projects.turbine.condenser.camel.routes;

import javax.validation.constraints.NotNull;

import org.apache.camel.Processor;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.web.ReplyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientInboundRoute extends BaseRoute {

  private static final Logger logger = LoggerFactory.getLogger(ClientInboundRoute.class);

  @Autowired
  @NotNull
  private Processor clientProcessor;

  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  // NOTE: this route handles inbound websocket messages sent to rabbitmq by the client
  // ideally spring would handle this in TestController, but that does not seem to work when
  // using rabbitmq as the stomp/websocket message broker...
  @Override
  public void configure() throws Exception {
    // bit of a hack, but the 'stub' camel component seems to recognise the 'queue' option name...
    String queueOptionName = "queue";
    if (infrastructureProperties.getAmqpCamelComponent().equals("stub")) {
      queueOptionName = queueOptionName + "stub";
    }

    String input = String.format("%s://%s/request?exchangeType=direct&%s=request",
        infrastructureProperties.getAmqpCamelComponent(),
        infrastructureProperties.getAmqpServer(),
        queueOptionName);

    from(input).id("client-requests")
        .convertBodyTo(ReplyResponse.class)
        .to(String.format("log:%s?showAll=true", this.getClass().getName()))
        .process(clientProcessor)
        .to(getWebsocket(infrastructureProperties.getWebsocketReply()))
        .end();

    logger.info(String.format("%s configured in camel context %s", this.getClass().getName(), getContext().getName()));
  }

}

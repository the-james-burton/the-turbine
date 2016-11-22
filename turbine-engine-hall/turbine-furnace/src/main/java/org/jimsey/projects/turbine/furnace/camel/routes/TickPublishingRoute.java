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
package org.jimsey.projects.turbine.furnace.camel.routes;

import javax.validation.constraints.NotNull;

import org.apache.camel.builder.RouteBuilder;
import org.jimsey.projects.turbine.furnace.component.InfrastructureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TickPublishingRoute extends RouteBuilder {

  private static final Logger logger = LoggerFactory.getLogger(TickPublishingRoute.class);

  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  public static final String IN = "direct:ticks.for.publishing";

  @Override
  public void configure() throws Exception {

    from(IN).id("tick-publish-route")
        .log(" ** tick for publishing")
        // .to(String.format("log:%s?showAll=true", this.getClass().getName()))
        .convertBodyTo(String.class)
        .multicast().parallelProcessing()
        .to(getOutput("ticks"))
        .end();

    logger.info(String.format("%s configured in camel context %s", this.getClass().getName(), getContext().getName()));
  }

  public String getOutput(String queueSuffix) {
    // bit of a hack, but the 'stub' camel component seems to recognise the 'queue' option name...
    String queueOptionName = "queue";
    if (infrastructureProperties.getAmqpCamelComponent().equals("stub")) {
      queueOptionName = queueOptionName + "stub";
    }
    String input = String.format("%s://%s/%s?exchangeType=topic&%s=%s.%s",
        infrastructureProperties.getAmqpCamelComponent(),
        infrastructureProperties.getAmqpServer(),
        infrastructureProperties.getAmqpTicksExchange(),
        queueOptionName,
        infrastructureProperties.getAmqpTicksQueue(),
        queueSuffix);
    return input;
  }

}

package org.jimsey.projects.turbine.spring.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AmqpRoute extends BaseRoute {

  private static final Logger logger = LoggerFactory.getLogger(AmqpRoute.class);

  public static final String IN = "direct:amqp.route.in";

  @Override
  public void configure() throws Exception {

    from(IN).id(IN)
        .to(String.format("log:%s?showAll=true&multiline=true", this.getClass().getName()))
        .end();

    logger.info(String.format("%s configured in camel context %s", this.getClass().getName(), getContext().getName()));
  }

}

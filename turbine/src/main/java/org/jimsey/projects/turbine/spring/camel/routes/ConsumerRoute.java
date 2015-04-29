package org.jimsey.projects.turbine.spring.camel.routes;

import javax.validation.constraints.NotNull;

import org.apache.camel.Processor;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("consumer")
public class ConsumerRoute extends BaseRoute {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerRoute.class);

  @Autowired
  @NotNull
  private InfrastructureProperties mInfrastructureProperties;

  @Autowired
  @NotNull
  private Processor mConsumerProcessor;

  @Override
  public void configure() throws Exception {

    from(mInfrastructureProperties.getAmqpExchange()).id("test route")
        .to(String.format("log:%s?showAll=true&multiline=true", this.getClass().getName()))
        .process(mConsumerProcessor)
        .end();

    logger.info(String.format("%s configured in camel context %s", this.getClass().getName(), getContext().getName()));
  }

}

package org.jimsey.projects.turbine.condenser.amqp;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class ExampleSender {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RabbitTemplate rabbitTemplate;

  public ExampleSender(RabbitTemplate rabbitTemplate) {
      this.rabbitTemplate = rabbitTemplate;
  }

  @ManagedOperation
  public void sendMessage(String message) throws Exception {
    logger.info(" ...> Spring AMQP Sending [{}]", message);
      rabbitTemplate.convertAndSend(AmqpSetup.queueName, message);
  }
}

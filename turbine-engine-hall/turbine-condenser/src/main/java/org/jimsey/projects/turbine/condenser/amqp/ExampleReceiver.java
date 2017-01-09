package org.jimsey.projects.turbine.condenser.amqp;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExampleReceiver {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void receiveMessage(String message) {
    logger.info(" ...> Spring AMQP Received [{}]", message);
  }

}

package org.jimsey.projects.turbine.spring.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsumerProcessor implements Processor {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerProcessor.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    Message message = exchange.getIn();
    String body = message.getMandatoryBody(String.class);
    logger.info("consumed: [{}]", body);
  }

}

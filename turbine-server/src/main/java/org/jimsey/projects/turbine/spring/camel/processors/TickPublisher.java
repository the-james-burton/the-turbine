package org.jimsey.projects.turbine.spring.camel.processors;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.service.TickProducer;
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

  public void publish(TickJson tick) {
    //series.addTick(tick);
    // TODO send symbols on their own topic...
    //String topic = String.format("%s.%s.%s", infrastructureProperties.getWebsocketTicks(), tick.getExchange(), tick.getSymbol());
    String topic = String.format("%s", infrastructureProperties.getWebsocketTicks());
    if ("ABC".equals(tick.getSymbol())) {
      websockets.convertAndSend(topic, tick);
      logger.info(String.format("websocket send: %s : %s", topic, tick.toString()));
    }

  }
  
}

package org.jimsey.projects.turbine.spring.web;

import org.jimsey.projects.turbine.spring.domain.test.Greeting;
import org.jimsey.projects.turbine.spring.domain.test.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(final HelloMessage message) throws Exception {
    Thread.sleep(3000); // simulated delay
    return new Greeting("Hello, " + message.getName() + "!");
  }

}
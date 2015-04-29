package org.jimsey.projects.turbine.spring.web;

import org.jimsey.projects.turbine.spring.service.Ping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class HelloController {

  @Autowired
  Ping ping;

  @RequestMapping("/ping")
  public Long ping() {
    return ping.ping();
  }

}
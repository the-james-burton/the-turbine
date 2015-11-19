package org.jimsey.projects.turbine.spring.web;

import javax.annotation.PostConstruct;

import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.service.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@EnableAutoConfiguration
@RequestMapping(TurbineConstants.REST_ROOT_TURBINE)
public class TurbineController {

  private static final Logger logger = LoggerFactory.getLogger(TurbineController.class);

  private static ObjectMapper json = new ObjectMapper();

  @Autowired
  private Ping ping;

  @PostConstruct
  public void init() throws Exception {
    logger.info("TurbineController: initialised");
  }

  @RequestMapping("/ping")
  public PingResponse ping() throws Exception {
    logger.info("ping()");
    return new PingResponse(ping.ping());
  }

}

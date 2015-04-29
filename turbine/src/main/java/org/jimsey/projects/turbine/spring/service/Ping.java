package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Ping extends BaseService {

  private static final Logger logger = LoggerFactory.getLogger(Ping.class);

  @Override
  @PostConstruct
  public void init() {
    logger.info("ping intialised");
  }

  public long ping() {
    return System.nanoTime();
  }

}

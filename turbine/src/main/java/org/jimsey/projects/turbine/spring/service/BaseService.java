package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class BaseService {

  private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

  @Autowired
  protected Environment mEnvironment;

  @PostConstruct
  public void init() {
    logger.info("BaseService init completed");
  }

}

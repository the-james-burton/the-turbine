package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.elasticsearch.repositories.IndicatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("consumer")
public class TurbineService {

  private static final Logger logger = LoggerFactory.getLogger(TurbineService.class);

  @Autowired
  @NotNull
  private IndicatorRepository indicatorRepository;

  @PostConstruct
  public void init() {
    logger.info("TurbineService: initialised");
  }
}

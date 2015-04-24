package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class BaseService {
	
	@Autowired
	protected Environment environment;

	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@PostConstruct
	public void init() {
		logger.info("BaseService init completed");
	}

}

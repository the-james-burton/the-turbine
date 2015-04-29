package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class Ping {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	Environment environment;
		
	@PostConstruct
	public void init() {
		logger.info("ping intialised");
	}
	
	public long ping() {
		return System.nanoTime();
	}

}

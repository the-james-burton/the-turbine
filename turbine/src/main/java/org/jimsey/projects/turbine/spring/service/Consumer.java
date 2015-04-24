package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("consumer")
public class Consumer extends BaseService {
		
	@PostConstruct
	public void init() {
		super.init();
		logger.info("consumer initialised");
	}
	
	public void consume(String message) {
		logger.info(String.format("consumed message: %s", message)); 
	}

}

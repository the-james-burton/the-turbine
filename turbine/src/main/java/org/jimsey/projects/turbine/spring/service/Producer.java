package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("producer")
//@PropertySource("classpath:conf/turbine.producer.properties")
//@PropertySource("classpath:conf/${environment}/turbine.producer.properties")
public class Producer extends BaseService {
	
	@PostConstruct
	public void init() {
		super.init();
		logger.info(String.format("direct default.property=%s", environment.getProperty("default.property")));
		logger.info(String.format("@Value default.property=%s", defaultProperty));
		logger.info(String.format("producer.property=%s", environment.getProperty("producer.property")));
		logger.info("producer initialised");
	}
	
	public String produce() {
		return "a new message";
	}

}

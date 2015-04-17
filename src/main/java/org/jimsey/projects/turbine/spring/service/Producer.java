package org.jimsey.projects.turbine.spring.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Profile("producer")
@PropertySource("classpath:conf/turbine.producer.properties")
@PropertySource("classpath:conf/${environment}/turbine.producer.properties")
public class Producer extends BaseService {

	@Autowired
	Environment environment;
		
	
	@PostConstruct
	public void init() {
		super.init();
		logger.info(String.format("direct default.property=%s", environment.getProperty("default.property")));
		logger.info(String.format("@Value default.property=%s", defaultProperty));
		logger.info(String.format("service.producer.hello=%s", environment.getProperty("service.producer.hello")));
		logger.info("producer initialised");
	}
	
	public String produce() {
		return "a new message";
	}

}

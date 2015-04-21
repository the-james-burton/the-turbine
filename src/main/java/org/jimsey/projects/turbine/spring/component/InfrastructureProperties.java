package org.jimsey.projects.turbine.spring.component;

import javax.annotation.PostConstruct;

import org.jimsey.projects.turbine.spring.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ConfigurationProperties(prefix="infrastructure")
public class InfrastructureProperties {

	private final Logger logger = LoggerFactory.getLogger(InfrastructureProperties.class);

	private String amqpDestination;
	
	private String commonProperty;

	@PostConstruct
	public void init() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		logger.info(objectMapper.writeValueAsString(this));
	}
	
	public String getCommonProperty() {
		return commonProperty;
	}

	public void setCommonProperty(String commonProperty) {
		this.commonProperty = commonProperty;
	}

	// ------------------------------------------
	public String getAmqpDestination() {
		return amqpDestination;
	}

	public void setAmqpDestination(String amqpDestination) {
		this.amqpDestination = amqpDestination;
	}
	
}

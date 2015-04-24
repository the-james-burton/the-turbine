package org.jimsey.projects.turbine.spring.component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ConfigurationProperties(prefix="infrastructure")
public class InfrastructureProperties {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureProperties.class);

	@NotNull
	private String amqpExchange;
	
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
	public String getAmqpExchange() {
		return amqpExchange;
	}

	public void setAmqpExchange(String amqpExchange) {
		this.amqpExchange = amqpExchange;
	}
	
}

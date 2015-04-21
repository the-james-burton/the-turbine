package org.jimsey.projects.turbine.spring.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="infrastructure")
public class InfrastructureProperties {

	private String amqpDestination;

	// ------------------------------------------
	public String getAmqpDestination() {
		return amqpDestination;
	}

	public void setAmqpDestination(String amqpDestination) {
		this.amqpDestination = amqpDestination;
		System.out.println(String.format("amqpDestination=%s", amqpDestination));
	}
	
}

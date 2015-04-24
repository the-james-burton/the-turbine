package org.jimsey.projects.turbine.spring.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.turbine.spring.camel.routes.AmqpRoute;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service
@Profile("producer")
@ManagedResource()
//@PropertySource("classpath:conf/turbine.producer.properties")
//@PropertySource("classpath:conf/${environment}/turbine.producer.properties")
public class Producer extends BaseService {
	
    @Autowired
    private CamelContext camel;
    
    @Autowired
    private InfrastructureProperties infrastructureProperties;
  
	@PostConstruct
	public void init() {
		super.init();
		logger.info(String.format("direct default.property=%s", environment.getProperty("default.property")));
		logger.info(String.format("@Value default.property=%s", defaultProperty));
		logger.info(String.format("producer.property=%s", environment.getProperty("producer.property")));
        logger.info(String.format("camel=%s", camel.getName()));
        logger.info(String.format("amqp=%s", infrastructureProperties.getAmqpExchange()));
		logger.info("producer initialised");
	}
	
	@ManagedOperation
	public void produce() {
	  ProducerTemplate producer = camel.createProducerTemplate();
	  
	  Map<String, Object> headers = new HashMap<String, Object>();
	  headers.put("test.header.1", "testing.header.one");
	  producer.sendBodyAndHeaders(AmqpRoute.IN, "test.body", headers);
	}

}

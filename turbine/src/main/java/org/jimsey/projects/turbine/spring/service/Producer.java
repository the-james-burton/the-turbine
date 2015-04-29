package org.jimsey.projects.turbine.spring.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("producer")
@ConfigurationProperties(prefix = "producer")
@ManagedResource()
public class Producer extends BaseService {

  @Autowired
  private CamelContext camel;

  @Autowired
  private InfrastructureProperties infrastructureProperties;

  @NotNull
  private Long period;

  @PostConstruct
  public void init() {
    super.init();
    logger.info(String.format("camel=%s", camel.getName()));
    logger.info(String.format("amqp=%s", infrastructureProperties.getAmqpExchange()));

    logger.info("producer initialised");
  }

  @ManagedOperation
  @Scheduled(fixedDelay=2000)
  public void produce() {
    ProducerTemplate producer = camel.createProducerTemplate();

    Map<String, Object> headers = new HashMap<String, Object>();
    String message = Double.toString(Math.random());
    headers.put("test.header.1", "testing.header.one");
    producer.sendBodyAndHeaders(infrastructureProperties.getAmqpExchange(), message, headers);
    logger.info("produced: [{}]", message);
  }

  // -----------------------------------------
  public Long getPeriod() {
    return period;
  }

  public void setPeriod(Long period) {
    this.period = period;
  }

}

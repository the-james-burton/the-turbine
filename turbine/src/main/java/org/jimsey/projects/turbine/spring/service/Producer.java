/**
 * The MIT License
 * Copyright (c) 2015 the-james-burton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jimsey.projects.turbine.spring.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(Producer.class);

  @Autowired
  @NotNull
  private CamelContext mCamel;

  @Autowired
  @NotNull
  private InfrastructureProperties mInfrastructureProperties;

  @NotNull
  private Long mPeriod;

  @Override
  @PostConstruct
  public void init() {
    super.init();

    logger.info(String.format("camel=%s", mCamel.getName()));
    logger.info(String.format("amqp=%s", mInfrastructureProperties.getAmqpExchange()));

    logger.info("producer initialised");
  }

  @ManagedOperation
  @Scheduled(fixedDelay = 2000)
  public void produce() {
    ProducerTemplate producer = mCamel.createProducerTemplate();

    Map<String, Object> headers = new HashMap<String, Object>();
    String message = Double.toString(Math.random());
    headers.put("test.header.1", "testing.header.one");
    producer.sendBodyAndHeaders(mInfrastructureProperties.getAmqpExchange(), message, headers);
    logger.info("produced: [{}]", message);
  }

  // -----------------------------------------
  public Long getPeriod() {
    return mPeriod;
  }

  public void setPeriod(final Long period) {
    this.mPeriod = period;
  }

}

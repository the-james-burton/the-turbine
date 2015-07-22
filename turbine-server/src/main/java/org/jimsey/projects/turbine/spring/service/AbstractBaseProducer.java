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

import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.scheduling.annotation.Scheduled;

public abstract class AbstractBaseProducer extends BaseService {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBaseProducer.class);

  @Autowired
  @NotNull
  protected CamelContext camel;
  
  @Autowired
  @NotNull
  protected DomainObjectGenerator rdog;
  
  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  public AbstractBaseProducer() {
    super();
  }

  @ManagedOperation
  @Scheduled(fixedDelay = TurbineConstants.PRODUCER_PERIOD)
  public void produce() {
    ProducerTemplate producer = camel.createProducerTemplate();

    Map<String, Object> headers = new HashMap<String, Object>();

    // byte[] body = DomainConverter.toBytes(quote, null);
    // byte[] body = mCamel.getTypeConverter().convertTo(byte[].class, object);
    Object body = createBody();
    headers.put(TurbineConstants.HEADER_FOR_OBJECT_TYPE, body.getClass().getName());

    String text = camel.getTypeConverter().convertTo(String.class, body);
    producer.sendBodyAndHeaders(getEndpointUri(), text, headers);
  }

  public abstract Object createBody();
  
  public abstract String getEndpointUri();
  
}
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
import org.jimsey.projects.camel.components.SpringSimpleMessagingConstants;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;

@ConfigurationProperties(prefix = "producer")
public class TickProducer {

  private static final Logger logger = LoggerFactory.getLogger(TickProducer.class);

  @Autowired
  @NotNull
  private CamelContext camel;

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  private DomainObjectGenerator rdog;

  public TickProducer(String market, String symbol) {
    this.rdog = new RandomDomainObjectGenerator(market, symbol);
  }

  @PostConstruct
  public void init() {
    logger.info(String.format("camel=%s", camel.getName()));
    logger.info("producer initialised");
  }

  public TickJson createBody() {
    TickJson tick = rdog.newTick();
    return tick;
  }

  @Scheduled(fixedDelay = TurbineConstants.PRODUCER_PERIOD)
  public void produce() {
    ProducerTemplate producer = camel.createProducerTemplate();

    Map<String, Object> headers = new HashMap<String, Object>();

    // byte[] body = DomainConverter.toBytes(quote, null);
    // byte[] body = mCamel.getTypeConverter().convertTo(byte[].class, object);
    TickJson tick = createBody();
    headers.put(TurbineConstants.HEADER_FOR_OBJECT_TYPE, tick.getClass().getName());

    // TODO should this be done in the TickProcessor instead?
    headers.put(SpringSimpleMessagingConstants.DESTINATION_SUFFIX,
        String.format(".%s.%s", tick.getMarket(), tick.getSymbol()));

    logger.info("producing: [body: {}, headers: {}]", tick.toString(), new JSONObject(headers));

    String text = camel.getTypeConverter().convertTo(String.class, tick);
    producer.sendBodyAndHeaders(infrastructureProperties.getAmqpTicks(), text, headers);
  }

}

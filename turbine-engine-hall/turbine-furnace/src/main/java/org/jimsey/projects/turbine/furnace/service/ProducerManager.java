/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
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
package org.jimsey.projects.turbine.furnace.service;

import static java.lang.String.*;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.furnace.TurbineFurnaceConstants;
import org.jimsey.projects.turbine.furnace.camel.routes.TickPublishingRoute;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javaslang.Function0;
import javaslang.collection.HashSet;
import javaslang.collection.Set;

@Service
@ConfigurationProperties(prefix = "producer")
@ManagedResource
public class ProducerManager {

  private static final Logger logger = LoggerFactory.getLogger(ProducerManager.class);

  @Autowired
  @NotNull
  private CamelContext camel;

  private ProducerTemplate camelProducer;

  private Set<TickProducer> producers = HashSet.empty();

  private Function0<Set<Ticker>> tickers = () -> producers.map(producer -> producer.getTicker());

  private Function0<Set<Ticker>> tickerCache = Function0.of(tickers).memoized();

  private final RestTemplate rest;
  
  public ProducerManager(RestTemplateBuilder restTemplateBuilder) {
    rest = restTemplateBuilder.build();
  }

  @PostConstruct
  public void init() {
    camelProducer = camel.createProducerTemplate();
    // TODO issue #5 replace this with an import of external stock market list
    findOrCreateTickProducer(Ticker.of("ABC.L"));
    findOrCreateTickProducer(Ticker.of("DEF.L"));
  }

  @Scheduled(fixedDelay = TurbineFurnaceConstants.PRODUCER_PERIOD)
  public void produceTicks() {
    producers
        .map(producer -> producer.createTick())
        .forEach(tick -> publishTick(tick));
  }

  public void produceTick(Ticker ticker) {
    producers
        .filter(producer -> ticker.equals(producer.getTicker()))
        .map(producer -> producer.createTick())
        .forEach(tick -> publishTick(tick));
  }

  @ManagedOperation
  public void produceTick(String ticker) {
    produceTick(Ticker.of(ticker));
  }

  public void publishTick(TickJson tick) {

    java.util.Map<String, Object> headers = new java.util.HashMap<>();

    // byte[] body = DomainConverter.toBytes(quote, null);
    // byte[] body = mCamel.getTypeConverter().convertTo(byte[].class, object);
    headers.put(TurbineFurnaceConstants.HEADER_FOR_OBJECT_TYPE, tick.getClass().getName());
    String text = camel.getTypeConverter().convertTo(String.class, tick);

    logger.info("publishing: [body: {}, headers: {}]", tick.toString(), new JSONObject(headers));
    camelProducer.sendBodyAndHeaders(TickPublishingRoute.IN, text, headers);
  }

  @ManagedOperation
  public String showProducers() {
    String result = producers
        .map(TickProducer::toString)
        .reduce((p1, p2) -> format("%s,%s", p1, p2));
    return result.toString();
  }

  public TickProducer addTickProducer(Ticker ticker) {
    logger.info("creating TickProducer object from Ticker:{}", ticker);
    TickProducer producer = TickProducer.of(rest, ticker);
    producers = producers.add(producer);
    tickerCache = Function0.of(tickers).memoized();
    return producer;
  }

  @ManagedOperation
  public TickProducer addTickProducer(String ticker) {
    return addTickProducer(Ticker.of(ticker));
  }

  public synchronized TickProducer findOrCreateTickProducer(Ticker ticker) {
    return producers.filter(producer -> producer.getTicker().equals(ticker)).getOrElse(() -> addTickProducer(ticker));
  }

  public Set<Ticker> getTickers() {
    return tickerCache.apply();
  }

}

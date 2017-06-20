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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.fuel.constants.TurbineFuelConstants;
import org.jimsey.projects.turbine.fuel.domain.ExchangeEnum;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.furnace.TurbineFurnaceConstants;
import org.jimsey.projects.turbine.furnace.amqp.AmqpPublisher;
import org.jimsey.projects.turbine.furnace.component.InfrastructureProperties;
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

import io.vavr.Function0;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;

@Service
@ConfigurationProperties(prefix = "producer")
@ManagedResource
public class ProducerManager {

  private static final Logger logger = LoggerFactory.getLogger(ProducerManager.class);

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructure;

  @Autowired
  @NotNull
  private ElasticsearchService elasticsearch;

  @Autowired
  @NotNull
  private AmqpPublisher amqpPublisher;

  private SortedSet<TickProducer> producers = TreeSet.empty();

  private Function0<Set<Ticker>> tickers = () -> producers.map(producer -> producer.getTicker());

  private Function0<Set<Ticker>> tickerCache = Function0.of(tickers).memoized();

  private final RestTemplate rest;

  public ProducerManager(RestTemplateBuilder restTemplateBuilder) {
    logger.info("received a RestTemplateBuilder:{}", restTemplateBuilder);
    rest = restTemplateBuilder.build();
  }

  @PostConstruct
  public void init() {

    // start with our preset list...
    Set<Ticker> tickersAll = HashSet.ofAll(TurbineFuelConstants.PRESET_TICKERS);

    // get any tickers from elasticsearch...
    List<Ticker> tickersFromEs = elasticsearch.findTickersByExchange(ExchangeEnum.LSE);
    // tickers.forEach(t -> logger.info(t.toString()));

    // NOTE: this doesn't trap uncheck exceptions...
    // List<Ticker> tickersFromEs = Try.of(() -> elasticsearch.findTickersByExchange(ExchangeEnum.LSE)).getOrElse(new
    // ArrayList<Ticker>());

    // TODO better way to do this with Option?
    if (tickersFromEs == null || tickersFromEs.isEmpty()) {
      logger.warn("WARNING: no tickers found in elasticsearch, only our hardcoded watches will be available to start with: {}",
          TurbineFuelConstants.PRESET_TICKERS);
    } else {
      tickersAll = TurbineFuelConstants.PRESET_TICKERS.addAll(tickersFromEs);
    }

    // create realtime simulations of watches and/or tickers from elasticsearch...
    tickersAll
        // restrict to only preset tickers for now...
        .filter(tick -> TurbineFuelConstants.PRESET_TICKERS.contains(tick))
        .map(tick -> findOrCreateTickProducer(tick))
        .forEach(producer -> producers = producers.add(producer));

    // find the most recent tick in ES for each ticker...
    tickersAll
        .map(ticker -> Tuple.of(ticker, elasticsearch.findMostRecentTick(ticker.getRic().toString())))
        .forEach(tick -> logger.info("mostRecent:{}", tick.toString()));

    // do some historic population...
    // producers
    // .flatMap(producer -> producer.fetchTicksFromYahooFinanceHistoric())
    // .peek(tick -> logger.info(tick.toString()))
    // .forEach(tick -> publishTick(tick));
  }

  @Scheduled(fixedDelay = TurbineFurnaceConstants.PRODUCER_PERIOD)
  public void produceTicks() {
    producers
        // don't do any realtime ticks for now...
        .filter(t -> false)
        .flatMap(producer -> producer.createTick())
        .forEach(tick -> publishTick(tick));
  }

  public void produceTick(Ticker ticker) {
    producers
        .filter(producer -> ticker.equals(producer.getTicker()))
        .flatMap(producer -> producer.createTick())
        .forEach(tick -> publishTick(tick));
  }

  @ManagedOperation
  public void produceTick(String ticker) {
    produceTick(Ticker.of(ticker));
  }

  public void publishTick(TickJson tick) {
    logger.info("publishing tick: {}", tick.toString());
    amqpPublisher.publishTick(tick);
  }

  @ManagedOperation
  public String showProducers() {
    String result = producers
        .map(TickProducer::toString)
        .reduce((p1, p2) -> format("%s,%s", p1, p2));
    return result.toString();
  }

  public synchronized TickProducer findOrCreateTickProducer(Ticker ticker) {
    return producers.filter(producer -> producer.getTicker().equals(ticker)).getOrElse(() -> addTickProducer(ticker));
  }

  public TickProducer addTickProducer(Ticker ticker) {
    logger.info("creating TickProducer object from Ticker:{}", ticker);
    TickProducer producer = TickProducer.of(rest, ticker,
        infrastructure.getFinanceYahooRealtimeUrl(), infrastructure.getFinanceYahooHistoricUrl());
    producers = producers.add(producer);
    tickerCache = Function0.of(tickers).memoized();
    return producer;
  }

  @ManagedOperation
  public TickProducer addTickProducer(String ticker) {
    return addTickProducer(Ticker.of(ticker));
  }

  public Set<Ticker> getTickers() {
    return tickerCache.apply();
  }

  public InfrastructureProperties getInfrastructure() {
    return infrastructure;
  }

  public void setInfrastructure(InfrastructureProperties infrastructure) {
    this.infrastructure = infrastructure;
  }

  public AmqpPublisher getAmqpPublisher() {
    return amqpPublisher;
  }

  public void setAmqpPublisher(AmqpPublisher amqpPublisher) {
    this.amqpPublisher = amqpPublisher;
  }

}

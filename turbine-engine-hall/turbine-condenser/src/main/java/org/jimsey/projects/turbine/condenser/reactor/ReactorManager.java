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
package org.jimsey.projects.turbine.condenser.reactor;

import static java.lang.String.*;
import static java.time.Duration.*;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.service.ElasticsearchService;
import org.jimsey.projects.turbine.condenser.service.TickerManager;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

@Component
public class ReactorManager {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @NotNull
  @Autowired
  private ElasticsearchService elasticsearch;

  @NotNull
  @Autowired
  private TickerManager tickerManager;

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  // handler for incoming tick strings from AMQP (should this be an EmitterProcessor?)...
  private TopicProcessor<String> msgs;

  // topics to enable multiple consumers...
  private TopicProcessor<TickJson> ticks;
  private TopicProcessor<TickJson> ticks2;

  private TopicProcessor<IndicatorJson> indicators;

  private TopicProcessor<StrategyJson> strategies;

  private final SimpMessagingTemplate websocket;

  public ReactorManager(SimpMessagingTemplate websocket) {
    this.websocket = websocket;
  }

  private final Function1<TickJson, String> websocketForTicks = (tick) -> format("%s.%s",
      infrastructureProperties.getWebsocketTicks(), tick.getRic());

  private final Function1<IndicatorJson, String> websocketForIndicators = (indicator) -> format("%s.%s.%s",
      infrastructureProperties.getWebsocketIndicators(), indicator.getRic(), indicator.getName());

  private final Function1<StrategyJson, String> websocketForStrategies = (strategy) -> format("%s.%s.%s",
      infrastructureProperties.getWebsocketStrategies(), strategy.getRic(), strategy.getName());

  @PostConstruct
  public void init() {

    logger.info(" ** ReactorManager init();");

    msgs = TopicProcessor.create("msgs");
    ticks = TopicProcessor.create("ticks");
    ticks2 = TopicProcessor.create("ticks2");
    indicators = TopicProcessor.create("indicators");
    strategies = TopicProcessor.create("strategies");

    ReactorTickSubscriber tickSubscriber = new ReactorTickSubscriber(
        "tickSubscriber", tickerManager, indicators, strategies);

    /*
     * This is where the stream processing happens.
     * Add more streams with topic for more subscribers...
     */
    msgs
        .doOnNext(msg -> logger.info(" reactor msgs -> {}", msg))
        .map(msg -> TickJson.of(msg))
        .subscribe(ticks);

    ticks
        // .subscribe(new ReactorTickSubscriber("tickSubscriber"));
        .doOnNext(tick -> logger.info(" reactor ticks -> tick:{}", tick))
        .buffer(ofSeconds(2))
        .flatMap(l -> Flux.fromIterable(l).sort())
        .subscribe(ticks2);

    ticks2
        .doOnNext(tick -> logger.info(" reactor ticks2 -> tick:{}", tick))
        .doOnNext(tick -> elasticsearch.indexTick(tick))
        .doOnNext(tick -> websocket.convertAndSend(websocketForTicks.apply(tick), tick.toString()))
        .subscribe(tickSubscriber);

    // --------------------------
    // alternative flow grouping by ticker, but doesn't work :(
    // ticks
    // .doOnNext(tick -> logger.info(" reactor ticks -> tick:{}", tick))
    // .doOnNext(tick -> elasticsearch.indexTick(tick))
    // .doOnNext(tick -> websocket.convertAndSend(websocketForTicks.apply(tick), tick.toString()))
    // .groupBy(tick -> tick.getTickerAsObject())
    // .doOnNext(tick -> logger.info("GROUP:{}", tick.key()))
    // .subscribe(new GroupedTickSubscriber(tickerManager, indicators, strategies));
    // --------------------------

    // indicators are strategies are generated by the ReactorTickSubscriber in response to a tick...
    indicators
        .doOnNext(indicator -> logger.info(" reactor indicators -> indicator:{}", indicator))
        .doOnNext(indicator -> elasticsearch.indexIndicator(indicator))
        .subscribe(
            indicator -> websocket.convertAndSend(websocketForIndicators.apply(indicator), indicator.toString()));

    strategies
        .doOnNext(strategy -> logger.info(" reactor strategies -> strategy:{}", strategy))
        .doOnNext(strategy -> elasticsearch.indexStrategy(strategy))
        .subscribe(strategy -> websocket.convertAndSend(websocketForStrategies.apply(strategy), strategy.toString()));

  }

  /**
   * TODO should the raw processor be exposed?
   * @return the reactor processor used to handle tick messages
   */
  public TopicProcessor<String> getInbound() {
    return msgs;
  }

  public ElasticsearchService getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchService elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

  public TickerManager getTickerManager() {
    return tickerManager;
  }

  public void setTickerManager(TickerManager tickerManager) {
    this.tickerManager = tickerManager;
  }

}

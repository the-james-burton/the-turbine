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

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import org.jimsey.projects.turbine.condenser.service.TickerManager;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.TopicProcessor;

/**
 * This was an attempt to make a subscriber that would take a stream of tickers for a particular ticker
 * Sadly, it does not seem to work. 
 * 
 * @author the-james-burton
 */
@Deprecated
public class GroupedTickSubscriber implements Subscriber<GroupedFlux<Ticker, TickJson>>, Subscription {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private Subscription subscription;

  // TODO don't really want to know about these... just passed on to the TickSubscriber
  private final TickerManager tickerManager;
  private final TopicProcessor<IndicatorJson> indicators;
  private final TopicProcessor<StrategyJson> strategies;

  private Map<Ticker, Subscriber<TickJson>> subscribers = new HashMap<>();

  public GroupedTickSubscriber(
      TickerManager tickerManager,
      TopicProcessor<IndicatorJson> indicators,
      TopicProcessor<StrategyJson> strategies) {
    this.tickerManager = tickerManager;
    this.indicators = indicators;
    this.strategies = strategies;
    logger.info("created GroupedTickSubscriber:{}", toString());
  }

  @Override
  public void onNext(GroupedFlux<Ticker, TickJson> groupedTicks) {
    logger.info("grouped doOnNext:{}:{}", toString());
    groupedTicks
        .doOnNext(x -> logger.info("grouped doOnNext: {}:{}", x, subscribers.get(groupedTicks.key())))
        .subscribe(
            tick -> subscribers.computeIfAbsent(tick.getTickerAsObject(),
                key -> new ReactorTickSubscriber(key.getRicAsString(), tickerManager, indicators, strategies)));
    // this.subscription.request(1);
  }

  @Override
  public void request(long n) {
    subscription.request(n);
  }

  @Override
  public void cancel() {
    subscription.cancel();
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    logger.info("grouped {} onSubscribe:{}", toString(), subscription.toString());
    this.subscription = subscription;
    this.subscription.request(1);
  }

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
    logger.error("grouped {} onError:{}", toString(), t.getMessage());
  }

  @Override
  public void onComplete() {
    logger.info("grouped {} onComplete", toString());
  }

}

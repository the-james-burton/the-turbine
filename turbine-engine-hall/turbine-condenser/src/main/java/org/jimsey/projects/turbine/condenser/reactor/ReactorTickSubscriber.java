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

import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.condenser.service.TickerManager;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;

import javaslang.collection.Stream;
import javaslang.control.Try;
import reactor.core.publisher.TopicProcessor;

/**
 * A reactor subscriber to process ticks
 * 
 * TODO
 *   1. make this typesafe, perhaps by using AMQP message converter before reactor
 *   2. should this instead be an instance of one of the various built in reactor subscribers?
 * @author the-james-burton
 */
public class ReactorTickSubscriber extends BaseSubscriber<TickJson> {

  private final TickerManager tickerManager;

  private final TopicProcessor<IndicatorJson> indicators;

  private final TopicProcessor<StrategyJson> strategies;

  public ReactorTickSubscriber(
      String name,
      TickerManager tickerManager,
      TopicProcessor<IndicatorJson> indicators,
      TopicProcessor<StrategyJson> strategies) {
    super(name);
    this.tickerManager = tickerManager;
    this.indicators = indicators;
    this.strategies = strategies;
  }

  @Override
  public void onNext(TickJson tick) {
    logger.info("{} onNext:{}", name, tick.toString());
    Ticker ticker = tick.getTickerAsObject();
    Stock stock = tickerManager.findOrCreateStock(ticker);

    tickerManager.findOrCreateStock(tick.getTickerAsObject()).receiveTick(tick);

    // TODO still need this?
    Try.run(() -> stock.awaitTick(tick.getTimestampAsObject()).await());

    Stream.ofAll(stock.getIndicators())
        .map(indicator -> indicator.run(tick))
        .forEach(indicatorJson -> indicators.onNext(indicatorJson));

    Stream.ofAll(stock.getStrategies())
        .map(strategy -> strategy.run(tick))
        .forEach(strategyJson -> strategies.onNext(strategyJson));

    super.onNext(tick);
  }

}

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
package org.jimsey.projects.turbine.condenser.domain;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.domain.indicators.BaseIndicator;
import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorInstance;
import org.jimsey.projects.turbine.condenser.domain.indicators.TurbineIndicator;
import org.jimsey.projects.turbine.condenser.domain.strategies.BaseStrategy;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.condenser.domain.strategies.TurbineStrategy;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

@ConfigurationProperties(prefix = "consumer")
public class Stock {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  // @Autowired
  // @NotNull
  // private TurbineService turbineService;

  /** this is not final because we may need to make a new TimeSeries in case of an out-of-order tick */
  private TimeSeries series;

  private final ClosePriceIndicator closePriceIndicator;

  private final List<TurbineIndicator> indicators = new ArrayList<>();

  private final List<TurbineStrategy> strategies = new ArrayList<>();

  private final Ticker ticker;

  // TODO periodically clean this up...
  // private Map<OffsetDateTime, CountDownLatch> ticksReceived = new ConcurrentHashMap<>();

  // private Cache<OffsetDateTime, CountDownLatch> ticksReceived;

  /** only one in flight operation per stock **/
  private ReentrantLock lock = new ReentrantLock();

  public Stock(
      final Ticker ticker,
      // final List<EnableTurbineIndicator> turbineIndicators,
      final List<IndicatorInstance> turbineIndicators,
      final List<EnableTurbineStrategy> turbineStrategies) {
    this.ticker = ticker;
    this.series = new TimeSeries(ticker.getRicAsString(), new ArrayList<Tick>());
    this.closePriceIndicator = new ClosePriceIndicator(series);
    lock.lock();
    Try.run(() -> prepare(turbineIndicators, turbineStrategies)).andFinally(() -> lock.unlock());
  }

  public static Stock of(
      final Ticker ticker,
      // final List<EnableTurbineIndicator> turbineIndicators,
      final List<IndicatorInstance> turbineIndicators,
      final List<EnableTurbineStrategy> turbineStrategies) {
    return new Stock(ticker, turbineIndicators, turbineStrategies);
  }

  private void prepare(
      final List<IndicatorInstance> turbineIndicators,
      final List<EnableTurbineStrategy> turbineStrategies) {

    // convert instances to real indicators...
    turbineIndicators.stream()
        .map(instance -> instantiateIndicator(instance))
        .filter(indicator -> indicator != null)
        .forEach(indicator -> indicators.add(indicator));

    turbineStrategies.stream().forEach(i -> {
      String className = String.format("%s.%s", EnableTurbineStrategy.class.getPackage().getName(), i.name());
      BaseStrategy strategy = (BaseStrategy) instantiateStrategy(className);
      strategies.add(strategy);
    });

    // initialise the cache...
    // ticksReceived = Caffeine.newBuilder()
    // .expireAfterWrite(60, TimeUnit.SECONDS)
    // .removalListener((OffsetDateTime k, CountDownLatch v, RemovalCause c) -> {
    // v.countDown();
    // logger.debug("cache expired: [ticker:{}, timestamp:{}]", ticker.getRic(), k.toString());
    // })
    // .build();

    logger.info("stock prepared:{}", ticker.getRicAsString());

  }

  private BaseIndicator instantiateIndicator(IndicatorInstance instance) {
    BaseIndicator result = null;
    try {
      Constructor<?> indicatorConstructor = Class.forName(instance.getClassname()).getConstructor(
          IndicatorInstance.class,
          TimeSeries.class,
          ClosePriceIndicator.class);
      result = (BaseIndicator) indicatorConstructor.newInstance(instance, series, closePriceIndicator);
      logger.debug("instantiated [{}]: {}", getTicker(), result.getInstance().getName());
    } catch (Exception e) {
      // TODO exceptions from the constructor in are missing in this exception!
      logger.warn("could not instantiate {} for [{}]: {}",
          instance.getClassname(), getTicker(), e.getMessage());
    }
    return result;
  }

  private Object instantiateStrategy(String name) {
    Object result = null;
    try {
      Constructor<?> indicatorConstructor = Class.forName(name).getConstructor(
          TimeSeries.class,
          ClosePriceIndicator.class);
      result = indicatorConstructor.newInstance(series, closePriceIndicator);
      logger.debug("instantiated [{}]: {}", getTicker(), result.getClass().getName());
    } catch (Exception e) {
      logger.warn("could not instantiate {} for [{}]: {}",
          name, getTicker(), e.getMessage());
    }
    return result;
  }

  public void recoverTicks(List<TickJson> ticks) {
    if (ticks == null) {
      logger.warn("ticker: {}, recoverTicks: no ticks to recover");
      return;
    }
    logger.info("ticker: {}, recoverTicks: {}", getTicker(), ticks.size());
    ticks.sort(TickJson.comparator);
    ticks.forEach(tick -> series.addTick(tick));
  }

  public void receiveTick(TickJson tick) {
    if (series.getTickCount() > 0) {
      logger.info("series timestamp:{}, receiveTick: {}",
          series.getLastTick().getBeginTime(), tick.getTimestamp());
    }
    lock.lock();
    Try.run(() -> processTick(tick)).andFinally(() -> lock.unlock());
  }

  private void processTick(TickJson tick) {
    try {
      series.addTick(tick);
    } catch (IllegalArgumentException e) {
      // Happens when trying to add an out-of-order tick
      logger.warn("out-of-order tick, will rebuild series: {}", tick);
      series = rebuildTimeSeriesWithAddTick(series, tick);
      logger.warn("successfully rebuild series:{}", series.getName());
    }
    // addOrGetLatch(tick.getTimestampAsObject()).countDown();
  }

  /**
   * The ticks in a Ta4J TimeSeries are final, immutable and hard to retrieve, so
   * in this method, we build a new TimeSeries in as best and simple a way as I can see
   * 
   * @param series the TimeSeries to append the out of order tick to
   * @param tick the out of order tick that cannot be directly added to the TimeSeries 
   * @return a new TimeSeries with the tick added in the right place
   */
  public static TimeSeries rebuildTimeSeriesWithAddTick(TimeSeries series, TickJson tick) {
    List<Tick> sortedTicks = Stream.range(0, series.getTickCount())
        .map(x -> series.getTick(x))
        .append(tick)
        .sorted()
        .toJavaList();
    return new TimeSeries(series.getName(), sortedTicks);
  }

  // private CountDownLatch addOrGetLatch(OffsetDateTime timestamp) {
  // // return ticksReceived.computeIfAbsent(timestamp, (key) -> new CountDownLatch(1));
  // return ticksReceived.get(timestamp, (key) -> new CountDownLatch(1));
  // }

  public List<TurbineIndicator> getIndicators() {
    return indicators;
  }

  public List<TurbineStrategy> getStrategies() {
    return strategies;
  }

  public Ticker getTicker() {
    return ticker;
  }

  // public CountDownLatch awaitTick(OffsetDateTime timestamp) {
  // return addOrGetLatch(timestamp);
  // }

}

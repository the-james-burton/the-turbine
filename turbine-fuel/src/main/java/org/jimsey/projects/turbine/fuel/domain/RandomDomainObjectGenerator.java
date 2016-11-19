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
package org.jimsey.projects.turbine.fuel.domain;

import static java.util.Comparator.*;
import static java.lang.String.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaslang.Function1;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.CharSeq;

public class RandomDomainObjectGenerator implements DomainObjectGenerator, Comparable<DomainObjectGenerator> {

  private static final String parsingExceptionText = "unable to parse %s as a valid ticker, it should be in the format 'ABC.L' where the L is a valid market in MarketEnum";

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Ticker ticker;
  
  private final MarketEnum market;
  
  private final CharSeq symbol;

  private TickJson tick;

  private IndicatorJson indicator;

  private StrategyJson strategy;

  // NOTE: for some reason, Eclipse or Java does not like this comparator builder on one line, hence the split...
  private final Comparator<DomainObjectGenerator> c1 = comparing(dog -> dog.getMarket().toString());

  private final Comparator<DomainObjectGenerator> comparator = c1.thenComparing(dog -> dog.getSymbol().toString());

  /**
   * given a string, this function will return a RuntimeException with a suitable message
   * to indicate that parsing the given string as a ticker failed
   */
  private final Function1<String, RuntimeException> parsingException = (m) -> new RuntimeException(format(parsingExceptionText, m));
  
  public RandomDomainObjectGenerator(String market, String symbol) {
    this(MarketEnum.valueOf(market), CharSeq.of(symbol));
  }

  public RandomDomainObjectGenerator(Ticker ticker) {
    this.ticker = ticker;
    this.symbol = ticker.getSymbol();
    this.market = ticker.getMarket();
    this.tick = createTick(this.market, this.symbol);
  }

  public RandomDomainObjectGenerator(String ticker) {
    // TODO same as function in DogKennel...
    CharSeq[] split = CharSeq.of(ticker).split("\\.");
    Tuple2<CharSeq, MarketEnum> tuple = Tuple.of(split[0], MarketEnum.fromExtension(split[1]).getOrElseThrow(() -> parsingException.apply(ticker)));
    this.symbol = tuple._1;
    this.market = tuple._2;
    this.ticker = Ticker.of(this.symbol, this.market);
    this.tick = createTick(this.market, this.symbol);
  }

  public RandomDomainObjectGenerator(MarketEnum market, CharSeq symbol) {
    Objects.requireNonNull(market);
    Objects.requireNonNull(symbol);
    this.market = market;
    this.symbol = symbol;
    this.ticker = Ticker.of(this.symbol, this.market);
    this.tick = createTick(this.market, this.symbol);
  }

  private TickJson createTick(MarketEnum market, CharSeq symbol) {
    return new TickJson(OffsetDateTime.now(), 100.0d, 101.0d, 90.0d, 100.0d, 5000.0d, symbol.toString(),
        market.toString(),
        OffsetDateTime.now().toString());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  @Override
  public TickJson newTick(OffsetDateTime date) {

    final double variation = 3.0d;

    double open = tick.getClosePrice().toDouble();
    double high = RandomUtils.nextDouble(open, open + variation);
    double low = RandomUtils.nextDouble(Math.max(0, open - variation), open);
    double close = RandomUtils.nextDouble(Math.max(0, low), high);
    double volume = RandomUtils.nextDouble(4000, 6000);

    tick = new TickJson(date, open, high, low, close, volume, symbol.toString(), market.toString(),
        OffsetDateTime.now().toString());
    return tick;
  }

  @Override
  public IndicatorJson newIndicator(OffsetDateTime date, String name) {

    final double variation = 3.0d;

    double open = tick.getClose();
    double high = tick.getHigh();
    double low = tick.getLow();
    double closePriceIndicator = tick.getClose();

    Map<String, Double> indicators = new HashMap<>();

    indicators.put("upper", RandomUtils.nextDouble(open, open + variation));
    indicators.put("lower", RandomUtils.nextDouble(Math.max(0, open - variation), open));
    indicators.put("middle", RandomUtils.nextDouble(Math.max(0, low), high));

    indicator = new IndicatorJson(
        date, closePriceIndicator, indicators,
        symbol.toString(), market.toString(), name, OffsetDateTime.now().toString());
    return indicator;
  }

  @Override
  public StrategyJson newStrategy(OffsetDateTime date, String name) {

    double close = tick.getClose();
    String action = "none";
    Integer amount = 0;
    Integer position = 0;
    Double cash = 250d;
    Double value = 0d;

    strategy = new StrategyJson(
        date, market.toString(), symbol.toString(), close,
        name, action, amount, position, cash, value, OffsetDateTime.now().toString());
    return strategy;
  }

  @Override
  public TickJson newTick() {
    tick = newTick(OffsetDateTime.now());
    return tick;
  }

  @Override
  public IndicatorJson newIndicator(String name) {
    return newIndicator(OffsetDateTime.now(), name);
  }

  @Override
  public StrategyJson newStrategy(String name) {
    return newStrategy(OffsetDateTime.now(), name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, symbol);
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof RandomDomainObjectGenerator)) {
      return false;
    }
    RandomDomainObjectGenerator that = (RandomDomainObjectGenerator) key;
    return Objects.equals(this.market, that.market)
        && Objects.equals(this.symbol, that.symbol);
  }

  @Override
  public int compareTo(DomainObjectGenerator that) {
    return comparator.compare(this, that);
  }

  
  
  @Override
  public MarketEnum getMarket() {
    return market;
  }

  @Override
  public CharSeq getSymbol() {
    return symbol;
  }

  @Override
  public Ticker getTicker() {
    return ticker;
  }

}

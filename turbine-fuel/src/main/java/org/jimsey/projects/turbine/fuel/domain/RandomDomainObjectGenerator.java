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

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ComparisonChain;

import javaslang.collection.CharSeq;

public class RandomDomainObjectGenerator implements DomainObjectGenerator, Comparable<DomainObjectGenerator> {

  private static final Logger logger = LoggerFactory.getLogger(RandomDomainObjectGenerator.class);

  private final CharSeq market;

  private final CharSeq symbol;

  private TickJson tick;

  private IndicatorJson indicator;

  private StrategyJson strategy;

  public RandomDomainObjectGenerator(String market, String symbol) {
    this(CharSeq.of(market), CharSeq.of(symbol));
  }

  public RandomDomainObjectGenerator(CharSeq market, CharSeq symbol) {
    this.market = market;
    this.symbol = symbol;
    this.tick = new TickJson(OffsetDateTime.now(), 100.0d, 101.0d, 90.0d, 100.0d, 5000.0d, this.symbol.toString(), this.market.toString(),
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

    tick = new TickJson(date, open, high, low, close, volume, symbol.toString(), market.toString(), OffsetDateTime.now().toString());
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
    return ComparisonChain.start()
        .compare(this.market.toString(), that.getMarket().toString())
        .compare(this.symbol.toString(), that.getSymbol().toString())
        .result();
  }

  @Override
  public CharSeq getMarket() {
    return market;
  }

  @Override
  public CharSeq getSymbol() {
    return symbol;
  }

}

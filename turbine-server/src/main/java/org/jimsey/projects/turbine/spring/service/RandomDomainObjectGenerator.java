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

import java.time.OffsetDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.domain.Quote;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.domain.Trade;
import org.jimsey.projects.turbine.spring.domain.Trader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

public class RandomDomainObjectGenerator implements DomainObjectGenerator {

  private static final Logger logger = LoggerFactory.getLogger(RandomDomainObjectGenerator.class);

  private final String exchange;
  
  private final String symbol;
    
  private TickJson tick;
  
  public RandomDomainObjectGenerator(String exchange, String symbol) {
    this.exchange = exchange;
    this.symbol = symbol;
    this.tick = new TickJson(OffsetDateTime.now(), 100.0d, 101.0d, 90.0d, 100.0d, 100.0d, this.symbol, this.exchange, OffsetDateTime.now().toString());
  }
  
  @Override
  public Instrument newInstrument() {
    Instrument instrument = new Instrument(RandomUtils.nextLong(1, 999), null);
    instrument.setCode(RandomStringUtils.randomAlphanumeric(10));
    return instrument;
  }

  @Override
  public Trader newTrader() {
    Trader trader = new Trader(RandomUtils.nextLong(1, 999), null);
    trader.setUsername(RandomStringUtils.randomAlphanumeric(10));
    return trader;
  }

  @Override
  public Quote newQuote() {
    Quote quote = new Quote(RandomUtils.nextLong(1, 999), null);
    quote.setInstrument(newInstrument());
    quote.setTrader(newTrader());
    quote.setBid(RandomUtils.nextDouble(10d, 99d));
    quote.setOffer(RandomUtils.nextDouble(10d, 99d));
    return quote;
  }

  @Override
  public Trade newTrade() {
    Trade trade = new Trade(RandomUtils.nextLong(1, 999), null);
    trade.setQuote(newQuote());
    trade.setBuyer(newTrader());
    trade.setSeller(newTrader());
    trade.setSize(RandomUtils.nextLong(1000, 9999));
    return trade;
  }

  @Override
  public TickJson newTick(OffsetDateTime date) {

    final double variation = 3.0d;

    double open = tick.getClosePrice().toDouble();
    double high = RandomUtils.nextDouble(open, open + variation);
    double low = RandomUtils.nextDouble(Math.max(0, open - variation), open);
    double close = RandomUtils.nextDouble(Math.max(0, low), high);
    double volume = RandomUtils.nextDouble(90, 110);

    tick = new TickJson(date, open, high, low, close, volume, this.symbol, this.exchange, OffsetDateTime.now().toString());
    return tick;
  }

  @Override
  public TickJson newTick() {

    tick = newTick(OffsetDateTime.now());
    return tick;
  }

}

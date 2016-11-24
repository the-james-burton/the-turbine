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

import java.util.Comparator;
import java.util.Objects;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.CharSeq;
import javaslang.control.Try;

public class Ticker implements Comparable<Ticker>{

  private final CharSeq ticker;
  
  private final CharSeq symbol;
  
  private final MarketEnum market;
  
  // ----------------------------
  public Ticker(CharSeq ticker) {
    this.ticker = ticker;
    CharSeq[] parts = ticker.split("\\.");
    Tuple2<CharSeq, CharSeq> tuple = Try.of(() -> Tuple.of(parts[0], parts[1])).getOrElseThrow(() -> new RuntimeException("unable to parse as ticker:" + ticker));
    this.symbol = tuple._1;
    this.market = MarketEnum.fromExtension(tuple._2).getOrElseThrow(() -> new RuntimeException("no MarketEnum for" + tuple._2));
  }

  
  
  public Ticker(CharSeq symbol, MarketEnum market) {
    this.symbol = symbol;
    this.market = market;
    this.ticker = CharSeq.of(String.format("%s.%s", symbol, market.getExtension()));
  }

  public Ticker(String ticker) {
    this(CharSeq.of(ticker));
  }

  public static Ticker of(CharSeq ticker) {
    return new Ticker(ticker);
  }

  public static Ticker of(String ticker) {
    return new Ticker(ticker);
  }

  public static Ticker of(CharSeq symbol, MarketEnum market) {
    return new Ticker(symbol, market);
  }

  public static Ticker of(String symbol, MarketEnum market) {
    return new Ticker(CharSeq.of(symbol), market);
  }

  public static Ticker of(String symbol, String market) {
    return new Ticker(CharSeq.of(symbol), MarketEnum.valueOf(market));
  }


  // ----------------------------
  private final Comparator<Ticker> comparator = Comparator
      .comparing(t -> t.getTicker().toString());

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof Ticker)) {
      return false;
    }
    Ticker that = (Ticker) key;
    return Objects.equals(this.ticker, that.ticker);
  }

  @Override
  public int compareTo(Ticker that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticker);
  }

  @Override
  public String toString() {
    // return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    return ticker.toString();
  }

  // ----------------------------
  public CharSeq getTicker() {
    return ticker;
  }

  public CharSeq getSymbol() {
    return symbol;
  }

  public MarketEnum getMarket() {
    return market;
  }

}

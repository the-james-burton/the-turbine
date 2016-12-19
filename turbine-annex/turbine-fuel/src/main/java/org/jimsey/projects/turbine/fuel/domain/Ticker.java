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

import static java.lang.String.*;

import java.util.Comparator;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.CharSeq;
import javaslang.control.Try;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class Ticker implements Comparable<Ticker>{

  private final CharSeq ticker;
  
  private final CharSeq symbol;
  
  private final CharSeq name;
  
  private final MarketEnum market;
  
  // ----------------------------
  public Ticker(CharSeq ticker, CharSeq name) {
    this.ticker = ticker;
    this.name = name;
    CharSeq[] parts = ticker.split("\\.");
    Tuple2<CharSeq, CharSeq> tuple = Try.of(() -> Tuple.of(parts[0], parts[1])).getOrElseThrow(() -> new RuntimeException("unable to parse as ticker:" + ticker));
    this.symbol = tuple._1;
    this.market = MarketEnum.fromExtension(tuple._2).getOrElseThrow(() -> new RuntimeException("no MarketEnum for extension:" + tuple._2));
  }
  
  public Ticker(CharSeq symbol, MarketEnum market, CharSeq name) {
    this.symbol = symbol;
    this.market = market;
    this.name = name;
    this.ticker = CharSeq.of(String.format("%s.%s", symbol, market.getExtension()));
  }

  public Ticker(String ticker) {
    this(CharSeq.of(ticker), CharSeq.empty());
  }

  public Ticker(CharSeq ticker) {
    this(ticker, CharSeq.empty());
  }

  public Ticker(String ticker, String name) {
    this(CharSeq.of(ticker), CharSeq.of(name));
  }

  // ------------------------------------------
  public static Ticker of(CharSeq ticker) {
    return new Ticker(ticker);
  }

  public static Ticker of(String ticker) {
    return new Ticker(ticker);
  }

  public static Ticker of(CharSeq ticker, CharSeq name) {
    return new Ticker(ticker, name);
  }

  public static Ticker of(String ticker, String name) {
    return new Ticker(ticker, name);
  }

  public static Ticker of(CharSeq symbol, MarketEnum market, CharSeq name) {
    return new Ticker(symbol, market, name);
  }

  public static Ticker of(String symbol, MarketEnum market, String name) {
    return new Ticker(CharSeq.of(symbol), market, CharSeq.of(name));
  }

  public static Ticker of(CharSeq symbol, CharSeq market, CharSeq name) {
    return Ticker.of(symbol, MarketEnum.fromMarket(market).getOrElseThrow(() -> new RuntimeException(format("no MarketEnum available for name %s", market))), name);
  }

  public static Ticker of(String symbol, String market, String name) {
    return Ticker.of(CharSeq.of(symbol), CharSeq.of(market), CharSeq.of(name));
  }

  // ----------------------------
  private final Comparator<Ticker> comparator = Comparator
      .comparing((Ticker t) -> t.getTicker().toString())
      .thenComparing(t -> t.getMarket())
      .thenComparing(t -> t.getName().toString());

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof Ticker)) {
      return false;
    }
    Ticker that = (Ticker) key;
    return Objects.equals(this.ticker, that.ticker)
        && Objects.equals(this.market, that.market)
        && Objects.equals(this.name, that.name);
  }

  @Override
  public int compareTo(Ticker that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticker, market, name);
  }

  @Override
  public String toString() {
    // return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    return ticker.toString();
  }

  // ----------------------------
  @JsonProperty("ticker")
  public String getTickerAsString() {
    return ticker.toString();
  }

  @JsonProperty("symbol")
  public String getSymbolAsString() {
    return symbol.toString();
  }

  @JsonProperty("market")
  public String getMarketAsString() {
    return market.toString();
  }

  @JsonIgnore
  public CharSeq getTicker() {
    return ticker;
  }

  @JsonIgnore
  public CharSeq getSymbol() {
    return symbol;
  }

  @JsonIgnore
  public MarketEnum getMarket() {
    return market;
  }

  public CharSeq getName() {
    return name;
  }

}

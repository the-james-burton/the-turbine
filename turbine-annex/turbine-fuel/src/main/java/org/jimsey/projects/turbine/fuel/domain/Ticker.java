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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class Ticker implements Comparable<Ticker> {

  private final CharSeq ric;

  private final CharSeq symbol;

  private final CharSeq name;

  private final ExchangeEnum exchange;

  private final OffsetDateTime timestamp;

  private static final ObjectMapper json = new ObjectMapper();

  // ----------------------------
  public Ticker(CharSeq ric, CharSeq name) {
    this.timestamp = OffsetDateTime.now();
    this.ric = ric;
    this.name = name;
    Seq<CharSeq> parts = ric.split("\\.");
    Tuple2<CharSeq, CharSeq> tuple = Try.of(() -> Tuple.of(parts.get(0), parts.get(1)))
        .getOrElseThrow(() -> new RuntimeException("unable to parse as ticker:" + ric));
    this.symbol = tuple._1;
    this.exchange = ExchangeEnum.fromExtension(tuple._2)
        .getOrElseThrow(() -> new RuntimeException("no ExchangeEnum for extension:" + tuple._2));
  }

  @JsonCreator
  public Ticker(
      @JsonProperty("timestamp") String timestamp,
      @JsonProperty("ric") String ric,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("exchange") String exchange,
      @JsonProperty("name") String name) {
    this.timestamp = OffsetDateTime.parse(timestamp);
    this.ric = CharSeq.of(ric);
    this.symbol = CharSeq.of(symbol);
    this.exchange = ExchangeEnum.valueOf(exchange);
    this.name = CharSeq.of(name);
  }

  public Ticker(CharSeq symbol, ExchangeEnum exchange, CharSeq name) {
    this.timestamp = OffsetDateTime.now();
    this.symbol = symbol;
    this.exchange = exchange;
    this.name = name;
    this.ric = CharSeq.of(String.format("%s.%s", symbol, exchange.getExtension()));
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
  public static Ticker of(CharSeq ric) {
    return new Ticker(ric);
  }

  public static Ticker of(String ric) {
    return new Ticker(ric);
  }

  public static Ticker of(CharSeq ric, CharSeq name) {
    return new Ticker(ric, name);
  }

  public static Ticker of(String ric, String name) {
    return new Ticker(ric, name);
  }

  public static Ticker of(CharSeq symbol, ExchangeEnum exchange, CharSeq name) {
    return new Ticker(symbol, exchange, name);
  }

  public static Ticker of(String symbol, ExchangeEnum exchange, String name) {
    return new Ticker(CharSeq.of(symbol), exchange, CharSeq.of(name));
  }

  public static Ticker of(CharSeq symbol, CharSeq exchange, CharSeq name) {
    return Ticker.of(symbol, ExchangeEnum.fromName(exchange)
        .getOrElseThrow(() -> new RuntimeException(format("no ExchangeEnum available for name %s", exchange))), name);
  }

  public static Ticker of(String symbol, String exchange, String name) {
    return Ticker.of(CharSeq.of(symbol), CharSeq.of(exchange), CharSeq.of(name));
  }

  // ----------------------------
  private final Comparator<Ticker> comparator = Comparator
      .comparing((Ticker t) -> t.getRicAsString())
      .thenComparing(t -> t.getExchange())
      .thenComparing(t -> t.getName().toString());

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof Ticker)) {
      return false;
    }
    Ticker that = (Ticker) key;
    return Objects.equals(this.ric, that.ric)
        && Objects.equals(this.exchange, that.exchange)
        && Objects.equals(this.name, that.name);
  }

  @Override
  public int compareTo(Ticker that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ric, exchange, name);
  }

  @Override
  public String toString() {
    // return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    // return ticker.toString();
    return Try.of(() -> json.writeValueAsString(this))
        .getOrElseThrow(e -> new RuntimeException(format("unable to write [%s] as JSON", ric)));
  }

  // ----------------------------
  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
  }

  @JsonIgnore
  public OffsetDateTime getTimestampAsObject() {
    return timestamp;
  }

  @JsonProperty("ric")
  public String getRicAsString() {
    return ric.toString();
  }

  @JsonProperty("symbol")
  public String getSymbolAsString() {
    return symbol.toString();
  }

  @JsonProperty("name")
  public String getNameAsString() {
    return name.toString();
  }

  @JsonProperty("exchange")
  public String getExchangeAsString() {
    return exchange.toString();
  }

  @JsonIgnore
  public CharSeq getRic() {
    return ric;
  }

  @JsonIgnore
  public CharSeq getSymbol() {
    return symbol;
  }

  @JsonIgnore
  public CharSeq getName() {
    return name;
  }

  @JsonIgnore
  public ExchangeEnum getExchange() {
    return exchange;
  }

}

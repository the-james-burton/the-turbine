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

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.verdelhan.ta4j.Tick;
import javaslang.control.Try;

// TODO this annotation (spring-data-elasticsearch only) forces the use of constants...
// @Document(
// indexName = TurbineFuelConstants.ELASTICSEARCH_INDEX_FOR_TICKS,
// type = TurbineFuelConstants.ELASTICSEARCH_TYPE_FOR_TICKS)
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class TickJson extends Tick implements Serializable {

  // TODO serialization does not work because Tick is a constructor immutable - maybe raise a request in ta4j...
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(TickJson.class);

  private static ObjectMapper json = new ObjectMapper();

  // TODO need a better id...
  // @Id
  private Long date;

  private OffsetDateTime timestamp;

  private Ticker ticker;

  public TickJson(
      OffsetDateTime date,
      double open, double high, double low, double close, double volume,
      Ticker ticker,
      String timestamp) {
    super(DateTime.parse(date.toString(), ISODateTimeFormat.dateTimeParser()), open, high, low, close, volume);
    this.timestamp = date;
    this.ticker = ticker;
    try {
      this.date = date.toInstant().toEpochMilli();
    } catch (Exception e) {
      logger.warn("Could not parse date: {}", date.toString());
    }
    try {
      this.timestamp = OffsetDateTime.parse(timestamp);
    } catch (Exception e) {
      logger.warn("Could not parse timestamp: {}", timestamp);
    }
  }

  @JsonCreator
  public TickJson(@JsonProperty("date") long date,
      @JsonProperty("open") double open,
      @JsonProperty("high") double high,
      @JsonProperty("low") double low,
      @JsonProperty("close") double close,
      @JsonProperty("volume") double volume,
      @JsonProperty("ric") String ric,
      @JsonProperty("timestamp") String timestamp) {
    this(OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()),
        open, high, low, close, volume, Ticker.of(ric), timestamp);
  }

  public static TickJson of(String tick) {
    return Try
        .of(() -> json.readValue(tick, TickJson.class))
        .getOrElseThrow(
            (e) -> new RuntimeException(String.format("unable to parse [%s] as a TickJson : [%s]", tick, e.getMessage())));
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof TickJson)) {
      return false;
    }
    TickJson that = (TickJson) key;
    return Objects.equals(this.date, that.date)
        && Objects.equals(this.timestamp, that.timestamp)
        && Objects.equals(this.ticker, that.ticker)
        && Objects.equals(this.getOpen(), that.getOpen())
        && Objects.equals(this.getHigh(), that.getHigh())
        && Objects.equals(this.getLow(), that.getLow())
        && Objects.equals(this.getClose(), that.getClose())
        && Objects.equals(this.getVol(), that.getVol());
  }

  // --------------------------------
  @JsonProperty("date")
  public long getDate() {
    return super.getEndTime().toInstant().getMillis();
  }

  @JsonProperty("close")
  public double getClose() {
    return super.getClosePrice().toDouble();
  }

  @JsonProperty("open")
  public double getOpen() {
    return super.getOpenPrice().toDouble();
  }

  @JsonProperty("high")
  public double getHigh() {
    return super.getMaxPrice().toDouble();
  }

  @JsonProperty("volume")
  public long getVol() {
    return Math.round(super.getVolume().toDouble());
  }

  @JsonProperty("low")
  public double getLow() {
    return super.getMinPrice().toDouble();
  }

  @JsonProperty("ric")
  public String getRic() {
    return ticker.getRicAsString();
  }

  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp.toString();
  }

  @JsonIgnore
  public OffsetDateTime getTimestampAsObject() {
    return timestamp;
  }

  @JsonIgnore
  public Ticker getTickerAsObject() {
    return ticker;
  }

  /**
   * @return a complete, unescaped string, for example:  {"date":1485798512013,"open":100.0,"high":102.02332696536376,"low":99.73993448169895,"close":101.24628125330254,"volume":5446,"ticker":"ABC.L","timestamp":"2017-01-30T17:48:32.017Z"}
   */
  @Override
  public String toString() {
    return Try.of(() -> json.writeValueAsString(this))
        .getOrElseThrow(e -> new RuntimeException(format("unable to write [%s] as String", this.toStringForElasticsearch())));
  }

  /**
   * @return string suitable for Elasticsearch, for example: {\"date\":1485798512013,\"open\":100.0,\"high\":102.02332696536376,\"low\":99.73993448169895,\"close\":101.24628125330254,\"volume\":5446,\"ticker\":\"ABC.L\"}
   */
  public String toStringForElasticsearch() {
    OffsetDateTime date = OffsetDateTime.parse(getEndTime().toString());

    // deliberately don't return the timestamp...
    return format(
        "{\"date\":%tQ,\"open\":%s,\"high\":%s,\"low\":%s,\"close\":%s,\"volume\":%.0f,\"ticker\":\"%s\"}",
        // "{\"date\":%tQ,\"open\":%.2f,\"high\":%.2f,\"low\":%.2f,\"close\":%.2f,\"volume\":%.0f,\"ticker\":\"%s\",\"timestamp\":\"%s\"}",
        // "{\\\"date\\\":%tQ,\\\"open\\\":%.2f,\\\"high\\\":%.2f,\\\"low\\\":%.2f,\\\"close\\\":%.2f,\\\"volume\\\":%.0f,\\\"ticker\\\":\\\"%s\\\"}",
        date,
        getOpenPrice().toDouble(),
        getMaxPrice().toDouble(),
        getMinPrice().toDouble(),
        getClosePrice().toDouble(),
        getVolume().toDouble(),
        ticker, timestamp.format(DateTimeFormatter.ISO_DATE_TIME));
  }

}

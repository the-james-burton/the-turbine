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

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public abstract class Entity implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(Entity.class);

  private static ObjectMapper json = new ObjectMapper();

  // TODO need a better id...
  @Id
  private Long date;

  private OffsetDateTime timestamp;

  private final String market;

  private final String symbol;

  private final Double close;

  private final String name;

  public Entity(
      OffsetDateTime date,
      double close,
      String symbol,
      String market,
      String name,
      String timestamp) {
    this.timestamp = date;
    this.close = close;
    this.symbol = symbol;
    this.market = market;
    this.name = name;
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

  @Override
  public String toString() {
    String result = null;
    try {
      // result = String.format("{\"%s\":%s}", this.getClass().getSimpleName(), json.writeValueAsString(this));
      result = json.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
    // return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    // return String.format("{\"%s\":%s}",
    // this.getClass().getSimpleName(),
    // ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE));
  }

  // -------------------------------
  @JsonProperty("date")
  public long getDate() {
    return date;
  }

  @JsonProperty("market")
  public String getMarket() {
    return market;
  }

  @JsonProperty("symbol")
  public String getSymbol() {
    return symbol;
  }

  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp.toString();
  }

  @JsonProperty("close")
  public Double getClose() {
    return close;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonIgnore
  public OffsetDateTime getTimestampAsObject() {
    return timestamp;
  }

}

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
package org.jimsey.projects.turbine.spring.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(
    indexName = TurbineConstants.ELASTICSEARCH_INDEX_FOR_STRATEGIES,
    type = TurbineConstants.ELASTICSEARCH_TYPE_FOR_STRATEGIES)
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class StrategyJson implements Serializable {

  // TODO implement base class for this and StockJson

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(StrategyJson.class);

  private static final ObjectMapper json = new ObjectMapper();

  @Id
  private Long date;

  private OffsetDateTime timestamp;

  private final String market;

  private final String symbol;

  private final Double close;

  private final String name;

  private final String action;

  private final Integer amount;

  private final Integer position;

  private final Double cash;

  private final Double value;

  public StrategyJson(
      OffsetDateTime date,
      String market,
      String symbol,
      Double close,
      String name,
      String action,
      Integer amount,
      Integer position,
      Double cash,
      Double value,
      String timestamp) {
    this.timestamp = date;
    this.close = close;
    this.symbol = symbol;
    this.market = market;
    this.name = name;
    this.action = action;
    this.amount = amount;
    this.position = position;
    this.cash = cash;
    this.value = value;
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
  public StrategyJson(
      @JsonProperty("date") long date,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("market") String market,
      @JsonProperty("close") double close,
      @JsonProperty("name") String name,
      @JsonProperty("action") String action,
      @JsonProperty("amount") Integer amount,
      @JsonProperty("position") Integer position,
      @JsonProperty("cash") Double cash,
      @JsonProperty("value") Double value,
      @JsonProperty("timestamp") String timestamp) {
    this(OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()),
        market, symbol, close, name, action, amount, position, cash, value, timestamp);
  }

  @Override
  public String toString() {
    String result = null;
    try {
      result = json.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  // -----------------------
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

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp.toString();
  }

  @JsonProperty("close")
  public Double getClose() {
    return close;
  }

  @JsonProperty("action")
  public String getAction() {
    return action;
  }

  @JsonIgnore
  public OffsetDateTime getTimestampAsObject() {
    return timestamp;
  }

  @JsonProperty("amount")
  public Integer getAmount() {
    return amount;
  }

  @JsonProperty("position")
  public Integer getPosition() {
    return position;
  }

  @JsonProperty("cash")
  public Double getCash() {
    return cash;
  }

  @JsonProperty("value")
  public Double getValue() {
    return value;
  }

  // ---------------------------------

}

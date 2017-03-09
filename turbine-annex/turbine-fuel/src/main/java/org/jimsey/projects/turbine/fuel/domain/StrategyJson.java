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
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO this annotation (spring-data-elasticsearch only) forces the use of constants...
// @Document(
// indexName = TurbineFuelConstants.ELASTICSEARCH_INDEX_FOR_STRATEGIES,
// type = TurbineFuelConstants.ELASTICSEARCH_TYPE_FOR_STRATEGIES)
public class StrategyJson extends Entity implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(StrategyJson.class);

  private final String action;

  private final Integer amount;

  private final Integer position;

  private final Double cash;

  private final Double value;

  public StrategyJson(
      OffsetDateTime date,
      Ticker ticker,
      Double close,
      String name,
      String action,
      Integer amount,
      Integer position,
      Double cash,
      Double value,
      String timestamp) {
    super(date, close, ticker, name, timestamp);
    this.action = action;
    this.amount = amount;
    this.position = position;
    this.cash = cash;
    this.value = value;
  }

  @JsonCreator
  public StrategyJson(
      @JsonProperty("date") long date,
      @JsonProperty("ric") String ric,
      @JsonProperty("close") double close,
      @JsonProperty("name") String name,
      @JsonProperty("action") String action,
      @JsonProperty("amount") Integer amount,
      @JsonProperty("position") Integer position,
      @JsonProperty("cash") Double cash,
      @JsonProperty("value") Double value,
      @JsonProperty("timestamp") String timestamp) {
    this(OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()),
        Ticker.of(ric), close, name, action, amount, position, cash, value, timestamp);
  }

  // -----------------------
  @JsonProperty("action")
  public String getAction() {
    return action;
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

}

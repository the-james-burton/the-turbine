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
import java.util.Map;

import org.jimsey.projects.turbine.fuel.constants.TurbineFuelConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO this annotation forces the use of constants...
@Document(
    indexName = TurbineFuelConstants.ELASTICSEARCH_INDEX_FOR_INDICATORS,
    type = TurbineFuelConstants.ELASTICSEARCH_TYPE_FOR_INDICATORS)
public class IndicatorJson extends Entity implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(IndicatorJson.class);

  private final Map<String, Double> indicators;

  public IndicatorJson(
      OffsetDateTime date,
      double close,
      Map<String, Double> indicators,
      String symbol,
      String market,
      String name,
      String timestamp) {
    super(date, close, symbol, market, name, timestamp);
    this.indicators = indicators;
  }

  @JsonCreator
  public IndicatorJson(
      @JsonProperty("date") long date,
      @JsonProperty("close") double close,
      @JsonProperty("indicators") Map<String, Double> indicators,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("market") String market,
      @JsonProperty("name") String name,
      @JsonProperty("timestamp") String timestamp) {
    this(OffsetDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()),
        close, indicators, symbol, market, name, timestamp);
  }

  // -----------------------
  @JsonProperty("indicators")
  public Map<String, Double> getIndicators() {
    return indicators;
  }

}
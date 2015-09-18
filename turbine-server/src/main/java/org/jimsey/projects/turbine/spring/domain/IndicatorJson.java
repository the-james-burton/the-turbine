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

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(indexName = "indicator", type = "indicator")
public class IndicatorJson extends Entity {

  private static final long serialVersionUID = 1L;

  private static ObjectMapper json = new ObjectMapper();

  private String indicator;

  private Double value;

  @PersistenceConstructor
  @JsonCreator
  public IndicatorJson(
      @JsonProperty("timestamp") String timestamp,
      @JsonProperty("exchange") String exchange,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("indicator") String indicator,
      @JsonProperty("value") Double value) {
    super(timestamp, exchange, symbol);
    this.indicator = indicator;
    this.value = value;
  }

  // -------------------------------------------
  public String getIndicator() {
    return indicator;
  }

  public Double getValue() {
    return value;
  }

}

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
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TurbineObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private static ObjectMapper json = new ObjectMapper();

  private List<Quote> quotes;
  
  private List<Instrument> instruments;
  
  private List<Trade> trades;
  
  private List<Trader> traders;

  // ------------------------------------
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
    //     this.getClass().getSimpleName(),
    //     ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE));
  }

  // ------------------------------------
  public List<Quote> getQuotes() {
    return quotes;
  }

  public void setQuotes(List<Quote> quotes) {
    this.quotes = quotes;
  }

  public List<Instrument> getInstruments() {
    return instruments;
  }

  public void setInstruments(List<Instrument> instruments) {
    this.instruments = instruments;
  }

  public List<Trade> getTrades() {
    return trades;
  }

  public void setTrades(List<Trade> trades) {
    this.trades = trades;
  }

  public List<Trader> getTraders() {
    return traders;
  }

  public void setTraders(List<Trader> traders) {
    this.traders = traders;
  }

}

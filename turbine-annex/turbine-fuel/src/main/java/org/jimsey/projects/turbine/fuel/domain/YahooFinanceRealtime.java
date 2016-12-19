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

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * NOTE: javadoc quirk: replace &amp; with just an ampersand to make the link work...
 * http://finance.yahoo.com/d/quotes.csv?f=nxohgav&amp;s=BHP.AX+BLT.L+AAPL
 * http://localhost:48006/finance/yahoo/realtime/ABC
 * 
 * "BHP BLT FPO","ASX",23.20,23.40,23.00,23.31,9714517
 * "BHP BILLITON PLC ORD $0.50","LSE",1225.00,1255.00,1222.00,1232.50,8947122
 * "Apple Inc.","NMS",114.43,114.56,113.51,113.87,13523517
 * 
 */
public class YahooFinanceRealtime {

  private final TickJson tick;

  private final TickerMetadata metadata;

  /**
   * @param metadata the metadata to use
   * @param tick the tick to base this object on
   */
  public YahooFinanceRealtime(TickerMetadata metadata, TickJson tick) {
    Objects.requireNonNull(metadata, "metadata must be provided");
    Objects.requireNonNull(tick, "tick must be provided");
    this.metadata = metadata;
    this.tick = tick;
  }

  public static YahooFinanceRealtime of(TickerMetadata metadata, TickJson tick) {
    return new YahooFinanceRealtime(metadata, tick);
  }
  

  /**
   * @param metadata the metadata to use
   * @param date the date
   * @param line  in this format: "ABCName","FTSE100",114.43,114.56,113.51,113.87,13523517
   */
  public YahooFinanceRealtime(TickerMetadata metadata, OffsetDateTime date, String line) {
    String[] parts = line.split(",");
    double open = Double.parseDouble(parts[2]);
    double high = Double.parseDouble(parts[3]);
    double low = Double.parseDouble(parts[4]);
    double close = Double.parseDouble(parts[5]);
    double volume = Double.parseDouble(parts[6]);
    this.metadata = metadata;
    this.tick = new TickJson(date, open, high, low, close, volume,
        metadata.getTicker(), date.toString());
  }

  public static YahooFinanceRealtime of(TickerMetadata metadata, OffsetDateTime date, String line) {
    return new YahooFinanceRealtime(metadata, date, line);
  }
  

  @Override
  public String toString() {
    return String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%d",
        metadata.getName(),
        metadata.getTicker().getMarket().toString(),
        getOpen(), getHigh(), getLow(), getClose(), getVol());
  }

  public TickerMetadata getMetadata() {
    return metadata;
  }
  
  public double getOpen() {
    return tick.getOpen();
  }

  public double getHigh() {
    return tick.getHigh();
  }

  public double getLow() {
    return tick.getLow();
  }

  public double getClose() {
    return tick.getClose();
  }

  public long getVol() {
    return tick.getVol();
  }

  
}
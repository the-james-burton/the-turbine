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

import javaslang.collection.List;
import javaslang.collection.Stream;

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

  /**
   * @param metadata the metadata to use
   * @param tick the tick to base this object on
   */
  public YahooFinanceRealtime(TickJson tick) {
    Objects.requireNonNull(tick, "tick must be provided");
    this.tick = tick;
  }

  public static YahooFinanceRealtime of(TickJson tick) {
    return new YahooFinanceRealtime(tick);
  }

  /**
   * @param metadata the metadata to use
   * @param date the date
   * @param line  in this format: "ABCName","LSE",114.43,114.56,113.51,113.87,13523517
   */
  public YahooFinanceRealtime(OffsetDateTime date, String line, Ticker ticker) {
    String[] parts = line.split(",");
    // Ticker ticker = Ticker.of(parts[0].replaceAll("\"", ""), parts[1].replaceAll("\"", ""));
    double open = Double.parseDouble(parts[2]);
    double high = Double.parseDouble(parts[3]);
    double low = Double.parseDouble(parts[4]);
    double close = Double.parseDouble(parts[5]);
    double volume = Double.parseDouble(parts[6]);
    this.tick = new TickJson(date, open, high, low, close, volume,
        ticker, date.toString());
  }

  public static YahooFinanceRealtime of(OffsetDateTime date, String line, Ticker ticker) {
    return new YahooFinanceRealtime(date, line, ticker);
  }

  public static List<YahooFinanceRealtime> of(OffsetDateTime date, String[] lines, Ticker[] tickers) {
    return Stream.of(lines)
        .zip(Stream.of(tickers))
        .map(tuple -> YahooFinanceRealtime.of(date, tuple._1, tuple._2)).toList();
  }

  @Override
  public String toString() {
    return String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%d",
        tick.getTickerAsObject().getName(), tick.getTickerAsObject().getExchangeAsString(),
        tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol());
  }

  public TickJson getTick() {
    return tick;
  }

}

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
package org.jimsey.projects.turbine.inlet.domain;

import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.fuel.domain.TickJson;

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

  private final SymbolMetadata metadata;

  /**
   * @param metadata the metadata to use
   * @param tick the tick to base this object on
   */
  public YahooFinanceRealtime(SymbolMetadata metadata, TickJson tick) {
    // this.metadata = symbolMetadataProvider.findMetadataForMarketAndSymbol(tick.getMarket(), tick.getSymbol());
    this.metadata = metadata;
    this.tick = tick;
  }

  /**
   * @param metadata the metadata to use
   * @param date the date
   * @param market market 
   * @param symbol symbol
   * @param line  in this format: "ABCName","FTSE100",114.43,114.56,113.51,113.87,13523517
   */
  public YahooFinanceRealtime(SymbolMetadata metadata, OffsetDateTime date, String market, String symbol, String line) {
    String[] parts = line.split(",");
    double open = Double.parseDouble(parts[2]);
    double high = Double.parseDouble(parts[3]);
    double low = Double.parseDouble(parts[4]);
    double close = Double.parseDouble(parts[5]);
    double volume = Double.parseDouble(parts[6]);
    this.metadata = metadata;
    this.tick = new TickJson(date, open, high, low, close, volume, market, symbol, date.toString());
  }

  @Override
  public String toString() {
    return String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%s",
        metadata.getName(), metadata.getMarket().toString(),
        getOpen(), getHigh(), getLow(), getClose(), getVol());
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

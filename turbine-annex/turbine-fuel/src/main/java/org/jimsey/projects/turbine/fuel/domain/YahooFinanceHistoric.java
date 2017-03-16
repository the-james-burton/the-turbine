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

import java.time.OffsetDateTime;
import java.util.Objects;

import javaslang.collection.List;

/**
 *  NOTE: javadoc quirk: replace &amp; with just an ampersand to make the link work...
 * http://real-chart.finance.yahoo.com/table.csv?s=DGE.L&amp;d=6&amp;e=5&amp;f=2016&amp;g=d&amp;a=6&amp;b=1&amp;c=1900&amp;ignore=.csv
 * 
 * Date,Open,High,Low,Close,Volume,Adj Close
 * 2016-07-05,165.40,166.70,158.50,158.50,30017600,158.50
 * 2016-07-04,171.80,172.458,166.20,167.50,19955300,167.50
 * 2016-07-01,173.40,176.70,165.337,169.70,38748700,169.70
 * 
 */
public class YahooFinanceHistoric {

  private final List<TickJson> ticks;

  /**
   * @param metadata the metadata to use
   * @param tick the tick to base this object on
   */
  public YahooFinanceHistoric(List<TickJson> ticks) {
    Objects.requireNonNull(ticks, "ticks must be provided");
    this.ticks = ticks;
  }

  public static YahooFinanceHistoric of(List<TickJson> ticks) {
    return new YahooFinanceHistoric(ticks);
  }

  /**
   * @param metadata the metadata to use
   * @param date the date
   * @param body  in this format:
   * 
   * Date,Open,High,Low,Close,Volume,Adj Close
   * 2016-07-05,165.40,166.70,158.50,158.50,30017600,158.50
   * 2016-07-04,171.80,172.458,166.20,167.50,19955300,167.50
   * 2016-07-01,173.40,176.70,165.337,169.70,38748700,169.70
   */
  public YahooFinanceHistoric(String body, Ticker ticker) {
    List<String> lines = List.of(body.split("\n")).drop(1);
    ticks = lines.map(line -> parseLine(line, ticker));
  }

  private TickJson parseLine(String line, Ticker ticker) {
    String[] parts = line.split(",");
    OffsetDateTime date = OffsetDateTime.parse(parts[0]);
    double open = Double.parseDouble(parts[1]);
    double high = Double.parseDouble(parts[2]);
    double low = Double.parseDouble(parts[3]);
    double close = Double.parseDouble(parts[4]);
    double volume = Double.parseDouble(parts[5]);
    return new TickJson(date, open, high, low, close, volume,
        ticker, date.toString());
  }

  public static YahooFinanceHistoric of(String line, Ticker ticker) {
    return new YahooFinanceHistoric(line, ticker);
  }

  @Override
  public String toString() {
    return "Date,Open,High,Low,Close,Volume,Adj Close\n".concat(
        ticks.map(tick -> format("%s,%.2f,%.2f,%.2f,%.2f,%d,%.2f",
            tick.getTimestampAsObject(),
            tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol(), tick.getClose()))
            .reduce((a, b) -> format("%s\n%s", a, b)));
  }

  public List<TickJson> getTicks() {
    return ticks;
  }

}

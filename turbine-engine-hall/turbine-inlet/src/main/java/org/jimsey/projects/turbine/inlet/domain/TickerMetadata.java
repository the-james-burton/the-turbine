/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
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

import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jimsey.projects.turbine.fuel.domain.Ticker;

import javaslang.collection.CharSeq;

public class TickerMetadata implements Comparable<TickerMetadata> {

  private final Ticker ticker;

  private final CharSeq name;

  // NOTE: for some reason, Eclipse or Java does not like this comparator builder on one line, hence the split...
  private final Comparator<TickerMetadata> c1 = Comparator.comparing(t -> t.getTicker().toString());
  private final Comparator<TickerMetadata> comparator = c1.thenComparing(t -> t.getName().toString());
  
  public TickerMetadata(Ticker ticker, CharSeq name) {
    this.ticker = ticker;
    this.name = name;
  }
  
  public TickerMetadata(Ticker ticker, String name) {
    this.ticker = ticker;
    this.name = CharSeq.of(name);
  }
  
  public static TickerMetadata of(Ticker ticker, CharSeq name) {
    return new TickerMetadata(ticker, name);
  }

  public static TickerMetadata of(Ticker ticker, String name) {
    return new TickerMetadata(ticker, name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTicker(), getName());
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof TickerMetadata)) {
      return false;
    }
    TickerMetadata that = (TickerMetadata) key;
    return Objects.equals(this.ticker, that.ticker)
        && Objects.equals(this.name, that.name);
  }

  @Override
  public int compareTo(TickerMetadata that) {
    return comparator.compare(this, that);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  public Ticker getTicker() {
    return ticker;
  }

  public CharSeq getName() {
    return name;
  }

}

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

import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SymbolMetadata implements Comparable<SymbolMetadata> {

  private final Market market;

  private final String symbol;

  private final String name;

  private final Comparator<SymbolMetadata> comparator = Comparator
      .comparing(SymbolMetadata::getMarket)
      .thenComparing(SymbolMetadata::getSymbol)
      .thenComparing(SymbolMetadata::getName);

  public SymbolMetadata(Market market, String symbol, String name) {
    this.symbol = symbol;
    this.name = name;
    this.market = market;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMarket(), getSymbol(), getName());
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof SymbolMetadata)) {
      return false;
    }
    SymbolMetadata that = (SymbolMetadata) key;
    return Objects.equals(this.getMarket(), that.getMarket())
        && Objects.equals(this.getSymbol(), that.getSymbol())
        && Objects.equals(this.getName(), that.getName());
  }

  @Override
  public int compareTo(SymbolMetadata that) {
    return comparator.compare(this, that);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  /**
   * returns a dot symbol code for this metadata
   * @return dot symbol code, eg. "ABC.L"
   */
  public String toDotSymbol() {
    return name + market.getExtension();
  }

  public String getSymbol() {
    return symbol;
  }

  public String getName() {
    return name;
  }

  public Market getMarket() {
    return market;
  }

}

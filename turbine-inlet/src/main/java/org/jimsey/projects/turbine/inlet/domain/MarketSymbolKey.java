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

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.ComparisonChain;

public class MarketSymbolKey implements Comparable<MarketSymbolKey> {

  private final String market;

  private final String symbol;

  public MarketSymbolKey(@NotNull String market, @NotNull String symbol) {
    this.market = market;
    this.symbol = symbol;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMarket(), getSymbol());
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof MarketSymbolKey)) {
      return false;
    }
    MarketSymbolKey that = (MarketSymbolKey) key;
    return Objects.equals(this.getMarket(), that.getMarket())
        && Objects.equals(this.getSymbol(), that.getSymbol());
  }

  @Override
  public int compareTo(MarketSymbolKey that) {
    return ComparisonChain.start()
        .compare(this.getMarket(), that.getMarket())
        .compare(this.getSymbol(), that.getSymbol())
        .result();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  // -----------------------------------------
  public String getMarket() {
    return market;
  }

  public String getSymbol() {
    return symbol;
  }

}

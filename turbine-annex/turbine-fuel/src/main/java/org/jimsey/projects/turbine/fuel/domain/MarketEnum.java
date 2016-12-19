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

import com.fasterxml.jackson.annotation.JsonFormat;

import javaslang.collection.CharSeq;
import javaslang.collection.Stream;
import javaslang.control.Option;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MarketEnum {

  ASX("ASXname", "AX"),
  FTSE100("FTSE100name", "L");

  private final CharSeq market;

  private final CharSeq extension;

  private MarketEnum(CharSeq market, CharSeq extension) {
    this.market = market;
    this.extension = extension;
  }

  private MarketEnum(String market, String extension) {
    this.market = CharSeq.of(market);
    this.extension = CharSeq.of(extension);
  }

  public static Option<MarketEnum> fromExtension(CharSeq extension) {
    return Stream.of(values()).find(m -> m.getExtension().eq(extension));
  }

  public static Option<MarketEnum> fromMarket(CharSeq market) {
    return Stream.of(values()).find(m -> m.getName().eq(market));
  }

  public CharSeq getName() {
    return market;
  }

  public CharSeq getExtension() {
    return extension;
  }

}

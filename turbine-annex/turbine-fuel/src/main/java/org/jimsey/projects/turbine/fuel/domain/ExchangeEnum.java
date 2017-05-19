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

import io.vavr.collection.CharSeq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExchangeEnum {

  ASX("Australian Stock Exchange", "AX"),
  LSE("London Stock Exchange", "L");

  private final CharSeq name;

  private final CharSeq extension;

  private ExchangeEnum(CharSeq name, CharSeq extension) {
    this.name = name;
    this.extension = extension;
  }

  private ExchangeEnum(String exchange, String extension) {
    this.name = CharSeq.of(exchange);
    this.extension = CharSeq.of(extension);
  }

  public static Option<ExchangeEnum> fromExtension(CharSeq extension) {
    return Stream.of(values()).find(m -> m.getExtension().eq(extension));
  }

  public static Option<ExchangeEnum> fromName(CharSeq exchange) {
    return Stream.of(values()).find(m -> m.getName().eq(exchange));
  }

  public CharSeq getName() {
    return name;
  }

  public CharSeq getExtension() {
    return extension;
  }

}

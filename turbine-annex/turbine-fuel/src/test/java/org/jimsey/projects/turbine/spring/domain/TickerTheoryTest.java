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

import org.jimsey.projects.turbine.fuel.domain.MarketEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.inspector.junit.ObjectTheories;
import org.junit.experimental.theories.DataPoint;

import javaslang.collection.CharSeq;

public class TickerTheoryTest extends ObjectTheories {

  public static final CharSeq ABC = CharSeq.of("ABC");

  public static final MarketEnum AX = MarketEnum.ASX;

  public static final MarketEnum L = MarketEnum.FTSE100;

  @DataPoint
  public static final Ticker ABC_AX = Ticker.of(ABC, AX);

  @DataPoint
  public static final Ticker ABC_L = Ticker.of(ABC, L);

  @DataPoint
  public static final Ticker DEF_AX = Ticker.of("DEF.AX");

  @DataPoint
  public static final Ticker DEF_L = Ticker.of("DEF.L");


}


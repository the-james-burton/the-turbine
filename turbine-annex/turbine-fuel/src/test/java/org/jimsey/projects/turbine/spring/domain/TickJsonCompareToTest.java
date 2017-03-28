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

import static org.jimsey.projects.turbine.spring.domain.TickJsonTheoryTest.*;
import static org.jimsey.projects.turbine.spring.domain.TickerTheoryTest.*;

import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.inspector.junit.ObjectTheories;
import org.junit.experimental.theories.DataPoint;

public class TickJsonCompareToTest extends ObjectTheories {

  @DataPoint
  public static final TickJson tjAA = new TickJson(now,
      R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString());

  @DataPoint
  public static final TickJson tjAB = new TickJson(now,
      R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickABA, now.toString());

  @DataPoint
  public static final TickJson tjBA = new TickJson(now.minusMinutes(1),
      R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString());

  @DataPoint
  public static final TickJson tjBB = new TickJson(now.minusMinutes(1),
      R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickABA, now.toString());

}

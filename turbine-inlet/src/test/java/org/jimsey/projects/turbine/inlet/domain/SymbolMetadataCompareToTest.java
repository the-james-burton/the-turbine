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

import org.jimsey.projects.turbine.inlet.test.CompareToTheory;
import org.junit.experimental.theories.DataPoint;

public class SymbolMetadataCompareToTest extends CompareToTheory {

  public static final String A = "A";

  public static final String B = "B";

  @DataPoint
  public static final SymbolMetadata MIN_MIN_MIN = new SymbolMetadata(Market.ASX, A, A);

  @DataPoint
  public static final SymbolMetadata MIN_MIN_MAX = new SymbolMetadata(Market.ASX, A, B);

  @DataPoint
  public static final SymbolMetadata MIN_MAX_MIN = new SymbolMetadata(Market.ASX, B, A);

  @DataPoint
  public static final SymbolMetadata MAX_MIN_MIN = new SymbolMetadata(Market.FTSE100, A, A);

  @DataPoint
  public static final SymbolMetadata EQUAL = new SymbolMetadata(Market.ASX, A, A);

}

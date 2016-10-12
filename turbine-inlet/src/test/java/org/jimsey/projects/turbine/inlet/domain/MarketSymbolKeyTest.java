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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MarketSymbolKeyTest {

  public static final String market = "market";

  public static final String symbol = "symbol";

  public static final String A = "A";

  public static final String B = "B";

  private final MarketSymbolKey mskAA = new MarketSymbolKey(A, A);

  private final MarketSymbolKey mskAB = new MarketSymbolKey(A, B);

  private final MarketSymbolKey mskBA = new MarketSymbolKey(B, A);

  private final MarketSymbolKey mskBB = new MarketSymbolKey(B, B);

  @Test
  public void testConstructor() {
    assertThat(mskAB, hasProperty(market, equalTo(A)));
    assertThat(mskAB, hasProperty(symbol, equalTo(B)));
  }

  @Test
  public void testHashCode() {
    assertThat(mskAA.hashCode(), equalTo(new MarketSymbolKey(A, A).hashCode()));
    assertThat(new MarketSymbolKey(A, A).hashCode(), equalTo(mskAA.hashCode()));

    assertThat(mskAA.hashCode(), not(equalTo(mskAB.hashCode())));
    assertThat(mskAA.hashCode(), not(equalTo(mskBA.hashCode())));
    assertThat(mskAA.hashCode(), not(equalTo(mskBB.hashCode())));
    assertThat(mskAB.hashCode(), not(equalTo(mskBA.hashCode())));
    assertThat(mskAB.hashCode(), not(equalTo(mskBB.hashCode())));
    assertThat(mskBA.hashCode(), not(equalTo(mskBB.hashCode())));
  }

  @Test
  public void testEquals() {
    // reflexive
    assertThat(mskAA, equalTo(mskAA));
    
    // symmetric
    assertThat(mskAA, equalTo(new MarketSymbolKey(A, A)));
    assertThat(new MarketSymbolKey(A, A), equalTo(mskAA));

    // consistent
    assertThat(mskAA, equalTo(new MarketSymbolKey(A, A)));
    assertThat(new MarketSymbolKey(A, A), equalTo(mskAA));
    
    assertThat(mskAA, not(equalTo(null)));
    
    assertThat(mskAA, not(equalTo(mskAB)));
    assertThat(mskAA, not(equalTo(mskBA)));
    assertThat(mskAA, not(equalTo(mskBB)));
    assertThat(mskAB, not(equalTo(mskBA)));
    assertThat(mskAB, not(equalTo(mskBB)));
    assertThat(mskBA, not(equalTo(mskBB)));
  }

  @Test
  public void testCompareTo() {
    assertThat(mskAA, equalTo(new MarketSymbolKey(A, A)));

    assertThat(mskAA, lessThan(mskAB));
    assertThat(mskAA, lessThan(mskBA));
    assertThat(mskAA, lessThan(mskBB));
    assertThat(mskAB, lessThan(mskBA));
    assertThat(mskAB, lessThan(mskBB));
    assertThat(mskBA, lessThan(mskBB));
  }

}

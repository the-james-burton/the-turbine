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
import static org.assertj.core.api.Assertions.*;

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
    // assertThat(mskAB, hasProperty(symbol, equalTo(B)));
    assertThat(mskAB).hasFieldOrPropertyWithValue(market, A);
    assertThat(mskAB).hasFieldOrPropertyWithValue(symbol, B);
  }

  @Test
  public void testHashCode() {
    // assertThat(mskAA.hashCode(), equalTo(new MarketSymbolKey(A, A).hashCode()));
    assertThat(mskAA.hashCode()).isEqualTo(new MarketSymbolKey(A, A).hashCode());
    assertThat(new MarketSymbolKey(A, A).hashCode()).isEqualTo(mskAA.hashCode());

    // assertThat(mskAA.hashCode(), not(equalTo(mskAB.hashCode())));
    assertThat(mskAA.hashCode()).isNotEqualTo(equalTo(mskAB.hashCode()));
    assertThat(mskAA.hashCode()).isNotEqualTo(mskBA.hashCode());
    assertThat(mskAA.hashCode()).isNotEqualTo(mskBB.hashCode());
    assertThat(mskAB.hashCode()).isNotEqualTo(mskBA.hashCode());
    assertThat(mskAB.hashCode()).isNotEqualTo(mskBB.hashCode());
    assertThat(mskBA.hashCode()).isNotEqualTo(mskBB.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive
    assertThat(mskAA).isEqualTo(mskAA);
    
    // symmetric
    assertThat(mskAA).isEqualTo(new MarketSymbolKey(A, A));
    assertThat(new MarketSymbolKey(A, A)).isEqualTo(mskAA);

    // consistent
    assertThat(mskAA).isEqualTo(new MarketSymbolKey(A, A));
    assertThat(new MarketSymbolKey(A, A)).isEqualTo(mskAA);
    
    assertThat(mskAA).isNotEqualTo(null);
    
    assertThat(mskAA).isNotEqualTo(mskAB);
    assertThat(mskAA).isNotEqualTo(mskBA);
    assertThat(mskAA).isNotEqualTo(mskBB);
    assertThat(mskAB).isNotEqualTo(mskBA);
    assertThat(mskAB).isNotEqualTo(mskBB);
    assertThat(mskBA).isNotEqualTo(mskBB);
  }

  @Test
  public void testCompareTo() {
    assertThat(mskAA).isEqualTo(new MarketSymbolKey(A, A));
    assertThat(mskAB).isEqualTo(new MarketSymbolKey(A, B));
    assertThat(mskBA).isEqualTo(new MarketSymbolKey(B, A));
    assertThat(mskBB).isEqualTo(new MarketSymbolKey(B, B));

    assertThat(mskAA).isLessThan(mskAB);
    assertThat(mskAA).isLessThan(mskBA);
    assertThat(mskAA).isLessThan(mskBB);
    assertThat(mskAB).isLessThan(mskBA);
    assertThat(mskAB).isLessThan(mskBB);
    assertThat(mskBA).isLessThan(mskBB);

    assertThat(mskAB).isGreaterThan(mskAA);
    assertThat(mskBA).isGreaterThan(mskAA);
    assertThat(mskBB).isGreaterThan(mskAA);
    assertThat(mskBA).isGreaterThan(mskAB);
    assertThat(mskBB).isGreaterThan(mskAB);
    assertThat(mskBB).isGreaterThan(mskBA);
}

}

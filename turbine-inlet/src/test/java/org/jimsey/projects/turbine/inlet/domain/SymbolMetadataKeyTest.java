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

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymbolMetadataKeyTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static final String A = "A";

  public static final String B = "B";
  
  private final SymbolMetadata smAAA = new SymbolMetadata(Market.ASX, A, A);

  private final SymbolMetadata smAAB = new SymbolMetadata(Market.ASX, A, B);

  private final SymbolMetadata smABA = new SymbolMetadata(Market.ASX, B, A);
  
  private final SymbolMetadata smBAA = new SymbolMetadata(Market.FTSE100, A, A);

  
  @Test
  public void testMarketCompareTo() {
    // make sure the ordering is correct in our enum...
    assertThat(Market.ASX).isLessThan(Market.FTSE100);
  }
  
  @Test
  public void testConstructor() {
    // check that the constructor correctly sets all properties...
    assertThat(smAAA).hasFieldOrPropertyWithValue("market", Market.ASX);
    assertThat(smAAA).hasFieldOrPropertyWithValue("symbol", A);
    assertThat(smAAA).hasFieldOrPropertyWithValue("name", A);
  }

  @Test
  public void testHashCode() {
    // check that the hash code is consistent across objects...
    assertThat(smAAA.hashCode()).isEqualTo(new SymbolMetadata(Market.ASX, A, A).hashCode());
    assertThat(new SymbolMetadata(Market.ASX, A, A).hashCode()).isEqualTo(smAAA.hashCode());

    // check that all fields are part of the hash code...
    assertThat(smAAA.hashCode()).isNotEqualTo(equalTo(smAAB.hashCode()));
    assertThat(smAAA.hashCode()).isNotEqualTo(smABA.hashCode());
    assertThat(smAAA.hashCode()).isNotEqualTo(smBAA.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive...
    assertThat(smAAA).isEqualTo(smAAA);
    
    // symmetric...
    assertThat(smAAA).isEqualTo(new SymbolMetadata(Market.ASX, A, A));
    assertThat(new SymbolMetadata(Market.ASX, A, A)).isEqualTo(smAAA);

    // consistent (same checks again)...
    assertThat(smAAA).isEqualTo(new SymbolMetadata(Market.ASX, A, A));
    assertThat(new SymbolMetadata(Market.ASX, A, A)).isEqualTo(smAAA);
    
    // transitive...
    SymbolMetadata smAAA2 = new SymbolMetadata(Market.ASX, A, A);
    SymbolMetadata smAAA3 = new SymbolMetadata(Market.ASX, A, A);
    
    assertThat(smAAA).isEqualTo(smAAA2);
    assertThat(smAAA2).isEqualTo(smAAA3);
    assertThat(smAAA).isEqualTo(smAAA3);
    
    // check not equal to null...
    assertThat(smAAA).isNotEqualTo(null);
    
    // check all properties are included in equals...
    assertThat(smAAA).isNotEqualTo(smAAB);
    assertThat(smAAA).isNotEqualTo(smABA);
    assertThat(smAAA).isNotEqualTo(smBAA);
  }

  @Test
  public void testCompareTo() {
    
    // check that all fields participate in compareTo...
    assertThat(smAAA).isLessThan(smAAB);
    assertThat(smAAA).isLessThan(smABA);
    assertThat(smAAA).isLessThan(smBAA);

    // check that the reverse is also true...
    assertThat(smAAB).isGreaterThan(smAAA);
    assertThat(smABA).isGreaterThan(smAAA);
    assertThat(smBAA).isGreaterThan(smAAA);
}

}

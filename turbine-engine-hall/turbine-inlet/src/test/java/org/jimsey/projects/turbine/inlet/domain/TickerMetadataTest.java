/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
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
import static org.jimsey.projects.turbine.inlet.domain.TickerMetadataTheoryTest.*;

import java.lang.invoke.MethodHandles;

import org.jimsey.projects.turbine.fuel.domain.MarketEnum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaslang.collection.CharSeq;

public class TickerMetadataTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  
  @Test
  public void testMarketCompareTo() {
    // make sure the ordering is correct in our enum...
    assertThat(MarketEnum.ASX).isLessThan(MarketEnum.FTSE100);
  }
  
  @Test
  public void testConstructor() {
    // check that the constructor correctly sets all properties...
    assertThat(smAA).hasFieldOrPropertyWithValue("ticker", ABC_AX);
    assertThat(smAA).hasFieldOrPropertyWithValue("name", CharSeq.of(NAME1));
  }

  @Test
  public void testHashCode() {
    // check that the hash code is consistent across objects...
    assertThat(smAA.hashCode()).isEqualTo(new TickerMetadata(ABC_AX, NAME1).hashCode());
    assertThat(new TickerMetadata(ABC_AX, NAME1).hashCode()).isEqualTo(smAA.hashCode());

    // check that all fields are part of the hash code...
    assertThat(smAA.hashCode()).isNotEqualTo(smAB.hashCode());
    assertThat(smAA.hashCode()).isNotEqualTo(smBA.hashCode());
    assertThat(smAA.hashCode()).isNotEqualTo(smBB.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive...
    assertThat(smAA).isEqualTo(smAA);
    
    // symmetric...
    assertThat(smAA).isEqualTo(new TickerMetadata(ABC_AX, NAME1));
    assertThat(new TickerMetadata(ABC_AX, NAME1)).isEqualTo(smAA);

    // consistent (same checks again)...
    assertThat(smAA).isEqualTo(new TickerMetadata(ABC_AX, NAME1));
    assertThat(new TickerMetadata(ABC_AX, NAME1)).isEqualTo(smAA);
    
    // transitive...
    TickerMetadata smAA2 = new TickerMetadata(ABC_AX, NAME1);
    TickerMetadata smAA3 = new TickerMetadata(ABC_AX, NAME1);
    
    assertThat(smAA).isEqualTo(smAA2);
    assertThat(smAA2).isEqualTo(smAA3);
    assertThat(smAA).isEqualTo(smAA3);
    
    // check not equal to null...
    assertThat(smAA).isNotEqualTo(null);
    
    // check all properties are included in equals...
    assertThat(smAA).isNotEqualTo(smAB);
    assertThat(smAA).isNotEqualTo(smAB);
    assertThat(smAA).isNotEqualTo(smBB);
  }

  @Test
  public void testCompareTo() {
    
    // check that all fields participate in compareTo...
    assertThat(smAA).isLessThan(smAB);
    assertThat(smAA).isLessThan(smBA);
    assertThat(smAA).isLessThan(smBB);

    // check that the reverse is also true...
    assertThat(smAB).isGreaterThan(smAA);
    assertThat(smBA).isGreaterThan(smAA);
    assertThat(smBB).isGreaterThan(smAA);
}

}

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

import static org.assertj.core.api.Assertions.*;
import static org.jimsey.projects.turbine.spring.domain.TickerTheoryTest.*;

import java.lang.invoke.MethodHandles;

import org.jimsey.projects.turbine.fuel.domain.MarketEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javaslang.collection.CharSeq;

public class TickerTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ObjectMapper json = new ObjectMapper();

  @Test
  public void testMarketCompareTo() {
    // make sure the ordering is correct in our enum...
    assertThat(MarketEnum.ASX).isLessThan(MarketEnum.FTSE100);
  }

  @Test
  public void testConstructor() {
    // check that the constructor correctly sets all properties...
    assertThat(ABC_AX).hasFieldOrPropertyWithValue("ticker", CharSeq.of(String.format("%s.%s", ABC, AX.getExtension())));
    assertThat(ABC_AX).hasFieldOrPropertyWithValue("symbol", ABC);
    assertThat(ABC_AX).hasFieldOrPropertyWithValue("market", AX);
  }

  @Test
  public void testHashCode() {
    // check that the hash code is consistent across objects...
    assertThat(ABC_AX.hashCode()).isEqualTo(new Ticker(ABC, AX).hashCode());
    assertThat(new Ticker(ABC, AX).hashCode()).isEqualTo(ABC_AX.hashCode());

    // check that all fields are part of the hash code...
    assertThat(ABC_AX.hashCode()).isNotEqualTo(ABC_L.hashCode());
    assertThat(ABC_AX.hashCode()).isNotEqualTo(DEF_AX.hashCode());
    assertThat(ABC_AX.hashCode()).isNotEqualTo(DEF_L.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive...
    assertThat(ABC_AX).isEqualTo(ABC_AX);

    // symmetric...
    assertThat(ABC_AX).isEqualTo(new Ticker(ABC, AX));
    assertThat(new Ticker(ABC, AX)).isEqualTo(ABC_AX);

    // consistent (same checks again)...
    assertThat(ABC_AX).isEqualTo(new Ticker(ABC, AX));
    assertThat(new Ticker(ABC, AX)).isEqualTo(ABC_AX);

    // transitive...
    Ticker ABC_AX2 = new Ticker(ABC, AX);
    Ticker ABC_AX3 = new Ticker(ABC, AX);

    assertThat(ABC_AX).isEqualTo(ABC_AX2);
    assertThat(ABC_AX2).isEqualTo(ABC_AX3);
    assertThat(ABC_AX).isEqualTo(ABC_AX3);

    // check not equal to null...
    assertThat(ABC_AX).isNotEqualTo(null);

    // check all properties are included in equals...
    assertThat(ABC_AX).isNotEqualTo(ABC_L);
    assertThat(ABC_AX).isNotEqualTo(DEF_AX);
    assertThat(ABC_AX).isNotEqualTo(DEF_L);
  }

  @Test
  public void testCompareTo() {

    // check that all fields participate in compareTo...
    assertThat(ABC_AX).isLessThan(ABC_L);
    assertThat(ABC_AX).isLessThan(DEF_AX);
    assertThat(ABC_AX).isLessThan(DEF_L);

    // check that the reverse is also true...
    assertThat(ABC_L).isGreaterThan(ABC_AX);
    assertThat(DEF_AX).isGreaterThan(ABC_AX);
    assertThat(DEF_L).isGreaterThan(ABC_AX);
  }

  @Test
  public void testJsonSerialise() throws JsonProcessingException {
    String result = json.writeValueAsString(ABC_L);
    logger.info(result);
  }
  
}

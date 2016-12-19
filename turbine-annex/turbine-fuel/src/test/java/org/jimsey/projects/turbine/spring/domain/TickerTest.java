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
    assertThat(tickAAA).hasFieldOrPropertyWithValue("ticker", CharSeq.of(String.format("%s.%s", TICK1, AX.getExtension())));
    assertThat(tickAAA).hasFieldOrPropertyWithValue("symbol", TICK1);
    assertThat(tickAAA).hasFieldOrPropertyWithValue("market", AX);
  }

  @Test
  public void testHashCode() {
    // check that the hash code is consistent across objects...
    assertThat(tickAAA.hashCode()).isEqualTo(new Ticker(TICK1, AX, NAME1).hashCode());
    assertThat(new Ticker(TICK1, AX, NAME1).hashCode()).isEqualTo(tickAAA.hashCode());

    // check that all fields are part of the hash code...
    assertThat(tickAAA.hashCode()).isNotEqualTo(tickAAB.hashCode());
    assertThat(tickAAA.hashCode()).isNotEqualTo(tickABA.hashCode());
    assertThat(tickAAA.hashCode()).isNotEqualTo(tickBAA.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive...
    assertThat(tickAAA).isEqualTo(tickAAA);

    // symmetric...
    assertThat(tickAAA).isEqualTo(new Ticker(TICK1, AX, NAME1));
    assertThat(new Ticker(TICK1, AX, NAME1)).isEqualTo(tickAAA);

    // consistent (same checks again)...
    assertThat(tickAAA).isEqualTo(new Ticker(TICK1, AX, NAME1));
    assertThat(new Ticker(TICK1, AX, NAME1)).isEqualTo(tickAAA);

    // transitive...
    Ticker ABC_AX2 = new Ticker(TICK1, AX, NAME1);
    Ticker ABC_AX3 = new Ticker(TICK1, AX, NAME1);

    assertThat(tickAAA).isEqualTo(ABC_AX2);
    assertThat(ABC_AX2).isEqualTo(ABC_AX3);
    assertThat(tickAAA).isEqualTo(ABC_AX3);

    // check not equal to null...
    assertThat(tickAAA).isNotEqualTo(null);

    // check all properties are included in equals...
    assertThat(tickAAA).isNotEqualTo(tickAAB);
    assertThat(tickAAA).isNotEqualTo(tickABA);
    assertThat(tickAAA).isNotEqualTo(tickBAA);
  }

  @Test
  public void testCompareTo() {

    // check that all fields participate in compareTo...
    assertThat(tickAAA).isLessThan(tickAAB);
    assertThat(tickAAA).isLessThan(tickABA);
    assertThat(tickAAA).isLessThan(tickBAA);

    // check that the reverse is also true...
    assertThat(tickAAB).isGreaterThan(tickAAA);
    assertThat(tickABA).isGreaterThan(tickAAA);
    assertThat(tickBAA).isGreaterThan(tickAAA);
  }

  @Test
  public void testJsonSerialise() throws JsonProcessingException {
    String result = json.writeValueAsString(tickAAB);
    logger.info(result);
  }
  
}

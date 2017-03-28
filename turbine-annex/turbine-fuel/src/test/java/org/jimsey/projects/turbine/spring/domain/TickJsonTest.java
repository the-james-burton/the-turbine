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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jimsey.projects.turbine.spring.domain.TickJsonTheoryTest.*;
import static org.jimsey.projects.turbine.spring.domain.TickerTheoryTest.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.SerializationUtils;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TickJsonTest {

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private static ObjectMapper json = new ObjectMapper();

  @Test
  public void testConstructor() {
    // check that the constructor correctly sets all properties...
    assertThat(tjAA).hasFieldOrPropertyWithValue("date", now.toInstant().toEpochMilli());
    assertThat(tjAA).hasFieldOrPropertyWithValue("ticker", tickAAA);
  }

  @Test
  public void testHashCode() {
    // check that the hash code is consistent across objects...
    assertThat(tjAA.hashCode()).isEqualTo(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString()).hashCode());
    assertThat(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString()).hashCode())
            .isEqualTo(tjAA.hashCode());

    // check that all fields are part of the hash code...
    assertThat(tjAA.hashCode()).isNotEqualTo(tjAB.hashCode());
    assertThat(tjAA.hashCode()).isNotEqualTo(tjBA.hashCode());
    assertThat(tjAA.hashCode()).isNotEqualTo(tjBB.hashCode());
  }

  @Test
  public void testEquals() {
    // reflexive...
    assertThat(tjAA).isEqualTo(tjAA);

    // symmetric...
    assertThat(tjAA).isEqualTo(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString()));
    assertThat(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString())).isEqualTo(tjAA);

    // consistent (same checks again)...
    assertThat(tjAA).isEqualTo(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString()));
    assertThat(new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString())).isEqualTo(tjAA);

    // transitive...
    TickJson ABC_AX2 = new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString());
    TickJson ABC_AX3 = new TickJson(now,
        R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), R.nextDouble(), tickAAA, now.toString());

    assertThat(tjAA).isEqualTo(ABC_AX2);
    assertThat(ABC_AX2).isEqualTo(ABC_AX3);
    assertThat(tjAA).isEqualTo(ABC_AX3);

    // check not equal to null...
    assertThat(tjAA).isNotEqualTo(null);

    // check all properties are included in equals...
    assertThat(tjAA).isNotEqualTo(tjAB);
    assertThat(tjAA).isNotEqualTo(tjBA);
    assertThat(tjAA).isNotEqualTo(tjBB);
  }

  @Test
  public void testCompareTo() {

    // check that all fields participate in compareTo...
    assertThat(tjAA).isLessThan(tjAB);
    assertThat(tjAA).isLessThan(tjBA);
    assertThat(tjAA).isLessThan(tjBB);

    // check that the reverse is also true...
    assertThat(tjAB).isGreaterThan(tjAA);
    assertThat(tjBA).isGreaterThan(tjAA);
    assertThat(tjBB).isGreaterThan(tjAA);
  }

  @Test
  public void testJsonCreator() {
    TickJson tick = new TickJson(1401174943825l, 99.52d, 99.58d, 98.99d, 99.08d, 100.0d,
        tickAAA.getRicAsString(), OffsetDateTime.now().toString());
    String jsonCreator = tick.toString();
    logger.info(jsonCreator);
    assertNotNull(jsonCreator);
  }

  @Test
  public void testJson() throws IOException {
    TickJson tick = new TickJson(OffsetDateTime.now(), 99.52d, 99.58d, 98.99d, 99.08d, 100.0d,
        tickAAA, OffsetDateTime.now().toString());
    String text = json.writeValueAsString(tick);
    tick = json.readValue(text, TickJson.class);
    logger.info(text);
    logger.info(tick.toString());
    assertEquals(text, tick.toString());

  }

  @Ignore
  @Test
  public void testSerializable() throws IOException {
    TickJson tick = new TickJson(OffsetDateTime.now(), 99.52d, 99.58d, 98.99d, 99.08d, 100.0d,
        tickAAA, OffsetDateTime.now().toString());
    byte[] bytes = SerializationUtils.serialize(tick);
    TickJson tick2 = (TickJson) SerializationUtils.deserialize(bytes);
    logger.info(tick.toString());
    logger.info(tick2.toString());
  }

}

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
package org.jimsey.project.turbine.spring.domain;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.spring.domain.IndicatorJson;
import org.jimsey.projects.turbine.spring.domain.IndicatorJson;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndicatorJsonTest {

  private static final String market = "FTSE100";

  private static final String symbol = "ABC";

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private static ObjectMapper json = new ObjectMapper();

  private IndicatorJson indicator;

  @Before
  public void before() {
  }

  @Test
  public void testConstructor() {
    indicator = new IndicatorJson(OffsetDateTime.now().toString(), market, symbol, "test-indicator", 123.4d);
    String asString = indicator.toString();
    logger.info(asString);
    assertNotNull(asString);
  }

  @Test
  public void testJson() throws IOException {
    indicator = new IndicatorJson(OffsetDateTime.now().toString(), market, symbol, "test-indicator", 123.4d);
    String text = json.writeValueAsString(indicator);
    logger.info(text);
    indicator = json.readValue(text, IndicatorJson.class);
    logger.info(indicator.toString());
    assertEquals(text, indicator.toString());

  }

  @Test
  public void testSerializable() throws IOException {
    indicator = new IndicatorJson(OffsetDateTime.now().toString(), market, symbol, "test-indicator", 123.4d);
    byte[] bytes = SerializationUtils.serialize(indicator);
    IndicatorJson indicator2 = (IndicatorJson) SerializationUtils.deserialize(bytes);
    logger.info(indicator.toString());
    logger.info(indicator2.toString());
  }

}

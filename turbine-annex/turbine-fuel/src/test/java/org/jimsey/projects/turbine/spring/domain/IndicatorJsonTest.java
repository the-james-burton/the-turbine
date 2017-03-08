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

import static org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndicatorJsonTest {

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private static ObjectMapper json = new ObjectMapper();

  private final Map<String, Double> indicators = new HashMap<>();

  private final String name = "testIndicatorName";

  private final Ticker TICKER = Ticker.of(ABC);

  @Before
  public void before() {
    // {"date": 1401174943825, "open": 99.52, "high": 99.58, "low": 98.99, "close": 99.08, "volume": 100},
    // this.stock = new STick(1401174943825l, 99.52d, 99.58d, 98.99d, 99.08d, 100.0d);
    indicators.put("testval1", 1234.0d);
    indicators.put("testval2", 2345.0d);
    indicators.put("testval13", 3456.0d);
  }

  @Test
  public void testJsonCreator() {
    IndicatorJson indicator = new IndicatorJson(1401174943825l, 100.0d, indicators,
        TICKER.getTickerAsString(), name, OffsetDateTime.now().toString());
    String jsonCreator = indicator.toString();
    logger.info(jsonCreator);
    assertNotNull(jsonCreator);
  }

  @Test
  public void testConstructor() {
    IndicatorJson indicator = new IndicatorJson(OffsetDateTime.now(), 100.0d, indicators,
        TICKER, name, OffsetDateTime.now().toString());
    String constructor = indicator.toString();
    logger.info(constructor);
    assertNotNull(constructor);
  }

  @Test
  public void testJson() throws IOException {
    IndicatorJson indicator = new IndicatorJson(OffsetDateTime.now(), 100.0d, indicators,
        TICKER, name, OffsetDateTime.now().toString());
    String text = json.writeValueAsString(indicator);
    indicator = json.readValue(text, IndicatorJson.class);
    logger.info(text);
    logger.info(indicator.toString());
    assertEquals(text, indicator.toString());

  }

  @Ignore
  @Test
  public void testSerializable() throws IOException {
    IndicatorJson indicator = new IndicatorJson(OffsetDateTime.now(), 100.0d, indicators,
        TICKER, name, OffsetDateTime.now().toString());
    byte[] bytes = SerializationUtils.serialize(indicator);
    TickJson stock2 = (TickJson) SerializationUtils.deserialize(bytes);
    logger.info(indicator.toString());
    logger.info(stock2.toString());
  }

}

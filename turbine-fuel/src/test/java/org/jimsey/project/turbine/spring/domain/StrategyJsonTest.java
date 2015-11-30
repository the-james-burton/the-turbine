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
package org.jimsey.project.turbine.spring.domain;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.SerializationUtils;
import org.jimsey.project.turbine.spring.TurbineTestConstants;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StrategyJsonTest {

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private static ObjectMapper json = new ObjectMapper();

  private StrategyJson strategy;

  @Before
  public void before() {
    // {"date":1401174943825,"symbol":"ABC","market":"FTSE100","close":100.0,"action":"enter","amount":1,"position":6,"cost":11.0,"value":14.0,"timestamp":"2015-11-06T18:21:47.263Z"}
  }

  @Test
  public void testJsonConstructor() {
    strategy = new StrategyJson(1401174943825l,
        TurbineTestConstants.SYMBOL, TurbineTestConstants.MARKET,
        100.0d, "exit", "myname", -1, 5, 10.0d, 15.0d, OffsetDateTime.now().toString());
    String jsonConstructor = strategy.toString();
    strategy = new StrategyJson(OffsetDateTime.now(),
        TurbineTestConstants.SYMBOL, TurbineTestConstants.MARKET,
        100.0d, "exit", "myname", -1, 5, 10.0d, 15.0d, OffsetDateTime.now().toString());
    String strategyConstructor = strategy.toString();
    logger.info(jsonConstructor);
    logger.info(strategyConstructor);
    assertNotNull(jsonConstructor);
    assertNotNull(strategyConstructor);
  }

  @Test
  public void testJson() throws IOException {
    strategy = new StrategyJson(1401174943825l,
        TurbineTestConstants.SYMBOL, TurbineTestConstants.MARKET,
        100.0d, "enter", "myname", 1, 6, 11.0d, 14.0d, OffsetDateTime.now().toString());
    String text = json.writeValueAsString(strategy);
    strategy = json.readValue(text, StrategyJson.class);
    logger.info(text);
    logger.info(strategy.toString());
    assertEquals(text, strategy.toString());

  }

  @Ignore
  @Test
  public void testSerializable() throws IOException {
    strategy = new StrategyJson(1401174943825l,
        TurbineTestConstants.SYMBOL, TurbineTestConstants.MARKET,
        100.0d, "enter", "myname", 1, 6, 11.0d, 14.0d, OffsetDateTime.now().toString());
    byte[] bytes = SerializationUtils.serialize(strategy);
    TickJson strategy2 = (TickJson) SerializationUtils.deserialize(bytes);
    logger.info(strategy.toString());
    logger.info(strategy2.toString());
  }

}

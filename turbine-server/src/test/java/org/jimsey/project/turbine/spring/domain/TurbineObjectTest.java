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

import java.io.IOException;

import org.jimsey.project.turbine.spring.TurbineTestConstants;
import org.jimsey.projects.turbine.spring.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.spring.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TurbineObjectTest {

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private ObjectMapper json;

  private DomainObjectGenerator rdog;

  @Before
  public void before() {
    json = new ObjectMapper();
    rdog = new RandomDomainObjectGenerator(TurbineTestConstants.MARKET, TurbineTestConstants.SYMBOL);
  }

  @Test
  public void testTickJson() throws JsonParseException, JsonMappingException, IOException {
    TickJson tick = rdog.newTick();
    String text = tick.toString();
    System.out.println(text);
    TickJson object = json.readValue(text, TickJson.class);
    System.out.println(object.toString());
  }
}

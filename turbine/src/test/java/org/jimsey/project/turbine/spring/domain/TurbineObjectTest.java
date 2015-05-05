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

import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.domain.Quote;
import org.jimsey.projects.turbine.spring.domain.Trader;
import org.jimsey.projects.turbine.spring.domain.TurbineObject;
import org.jimsey.projects.turbine.spring.service.DomainObjectGenerator;
import org.jimsey.projects.turbine.spring.service.RandomDomainObjectGenerator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TurbineObjectTest {

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private TurbineObject object;

  private ObjectMapper json;
  
  private DomainObjectGenerator rdog;

  @Before
  public void before() {
    json = new ObjectMapper();
    rdog = new RandomDomainObjectGenerator();
    
    object = new TurbineObject();
    object.setQuote(rdog.newQuote());

  }

  @Test
  public void testTurbineObject() throws JsonParseException, JsonMappingException, IOException {
    String text = object.toString();
    System.out.println(text);
    TurbineObject object = json.readValue(text, TurbineObject.class);
    System.out.println(object.toString());
    /*
    JsonNode node = json.readTree(text);
    switch (EntitiesEnum.valueOf(node.fieldNames().next())) {
    case Quote:
      Quote quote = json.treeToValue(node.elements().next(), Quote.class);
      System.out.println(quote.toString());
      break;
    case Instrument:
      Instrument instrument = json.treeToValue(node.elements().next(), Instrument.class);
      System.out.println(instrument.toString());
      break;
    default:
    }
    */
  }
}

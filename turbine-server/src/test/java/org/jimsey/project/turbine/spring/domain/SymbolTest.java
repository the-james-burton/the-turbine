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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.spring.domain.Symbol;
import org.jimsey.projects.turbine.spring.service.DomainObjectGenerator;
import org.jimsey.projects.turbine.spring.service.RandomDomainObjectGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SymbolTest {

  private static final String exchange = "FTSE100";

  private static final String symbol = "ABC";

  private static final Logger logger = LoggerFactory.getLogger(TurbineObjectTest.class);

  private static ObjectMapper json;

  private DomainObjectGenerator rdog;

  private Symbol sym;

  @Before
  public void before() {
    logger.info("new ObjectMapper...");
    json = new ObjectMapper();
    logger.info("new RandomDomainObjectGenerator({}, {})...", exchange, symbol);
    rdog = new RandomDomainObjectGenerator(exchange, symbol);
    logger.info("new Symbol({}, {})...", exchange, symbol);
    sym = new Symbol(exchange, symbol);
    for (OffsetDateTime date = OffsetDateTime.now().minusMinutes(1); date
        .isBefore(OffsetDateTime.now()); date = date.plusSeconds(1)) {
      sym.receiveTick(rdog.newTick(date));
    }
    logger.info("symbol initialized...");
  }

  @Test
  public void testToString() {
    String text = sym.toString();
    logger.info("testToString:{}", text);
    assertNotNull(text);
  }

  @Test
  public void testJson() throws IOException {
    logger.info("json.writeValueAsString()...");
    String text = json.writeValueAsString(sym);
    logger.info("testJson:{}", text);
    // Symbol is not deserializable, so we will not test json.readvalue
    logger.info("assertions...");
    // {"exchange":"FTSE100","symbol":"ABC","closePriceIndicator":106.05653150666,"bollingerBandsMiddleIndicator":106.05653150666,"bollingerBandsLowerIndicator":-106.05653150666,"bollingerBandsUpperIndicator":318.16959451997997,"timestamp":"2015-10-12T17:27:13.870+01:00"}
    assertThat(text, containsString("exchange"));
    assertThat(text, containsString("symbol"));
    assertThat(text, containsString("closePriceIndicator"));
    assertThat(text, containsString("bollingerBandsMiddleIndicator"));
    assertThat(text, containsString("bollingerBandsLowerIndicator"));
    assertThat(text, containsString("bollingerBandsUpperIndicator"));
    assertThat(text, containsString("timestamp"));

  }

  @Ignore
  @Test
  public void testSerializable() throws IOException {
    byte[] bytes = SerializationUtils.serialize(sym);
    // ta4j indicators are not Serializable, so don't test just now...
    assertNotNull(bytes);
  }

}

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
package org.jimsey.projects.turbine.condenser.domain.indicators;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndicatorInstanceTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static ObjectMapper json = new ObjectMapper();

  @Test
  public void testJson() throws IOException {
    IndicatorInstance indicator = new IndicatorInstance("testname", 5, 6, 7, false);
    String text = json.writeValueAsString(indicator);
    IndicatorInstance indicator2 = json.readValue(text, IndicatorInstance.class);
    String text2 = json.writeValueAsString(indicator2);
    logger.info(text2);
    assertThat(text).isEqualTo(text2);
    assertThat(indicator).isEqualTo(indicator);
  }

  @Test
  public void testSerializable() throws IOException {
    IndicatorInstance indicator = new IndicatorInstance("testname", 5, 6, 7, false);
    byte[] bytes = SerializationUtils.serialize(indicator);
    IndicatorInstance indicator2 = (IndicatorInstance) SerializationUtils.deserialize(bytes);
    assertThat(indicator.getName()).isEqualTo(indicator2.getName());
    assertThat(indicator).isEqualTo(indicator);
  }

  @Test
  public void testGenerateName() throws Exception {
    logger.info(new IndicatorInstance("indicator.one", 5, 6, 7, false).getName());
    logger.info(new IndicatorInstance("indicator.two", 5, 6, null, false).getName());
    logger.info(new IndicatorInstance("indicator.three", 5, null, null, false).getName());
    logger.info(new IndicatorInstance("indicator.four", null, null, null, false).getName());
  }

}

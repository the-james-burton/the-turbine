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
package org.jimsey.projects.turbine.inspector.matchers;

import static org.hamcrest.CoreMatchers.*;
import static org.jimsey.projects.turbine.inspector.matchers.TurbineMatchers.*;
import static org.junit.Assert.*;

import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurbineMatchersTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Test
  public void testIsNumeric() {
    logger.debug("testIsNumeric");
    
    assertThat("1", isNumeric());
    assertThat("1.2", isNumeric());
    assertThat("100000.10000", isNumeric());
    assertThat("0.00001", isNumeric());
    
    assertThat("1 a", not(isNumeric()));
    assertThat("one", not(isNumeric()));
    assertThat(".", not(isNumeric()));
    assertThat("", not(isNumeric()));
    assertThat(null, not(isNumeric()));
    
    // will fail if matcher is using NumberUtils.isNumber()
    assertThat("001.2", isNumeric());

  }
  
}

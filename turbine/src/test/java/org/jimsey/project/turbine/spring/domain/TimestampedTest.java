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

import java.time.ZoneId;

import org.jimsey.projects.turbine.spring.domain.Timestamped;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TimestampedTest {

  private Timestamped timestamped;

  @Before
  public void before() {
    this.timestamped = new Timestamped(null);
  }

  @Ignore
  @Test
  public void showZones() {
    for (String zoneId : ZoneId.getAvailableZoneIds()) {
      System.out.println(zoneId);
    }
  }

  @Test
  public void testTimestampedParsing() {
    String text = timestamped.getTimestampAsString();
    Timestamped parsed = new Timestamped(text);
    assertTrue(text.equals(parsed.getTimestampAsString()));
  }
  
}

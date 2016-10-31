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
package org.jimsey.projects.turbine.inlet.domain;

import static org.assertj.core.api.Assertions.*;
import static org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooFinanceRealtimeTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final SymbolMetadataProvider smp = new SymbolMetadataProviderImpl();
  
  private final DomainObjectGenerator dog = new RandomDomainObjectGenerator(FTSE100, ABC);
  
  @Test
  public void testRoundTripForString() {
    OffsetDateTime date = OffsetDateTime.now();
    SymbolMetadata metadata = smp.findMetadataForTicker("ABC.L");
    String expected = String.format("\"%s\",\"%s\",114.43,114.56,113.51,113.87,13523517", metadata.getName(), metadata.getMarket());
    YahooFinanceRealtime yfr = new YahooFinanceRealtime(metadata, date, FTSE100, ABC, expected);
    String actual = yfr.toString();
    assertThat(actual).isEqualTo(expected);
  }
  
  @Test
  public void testRoundTripForTickJson() {
    OffsetDateTime date = OffsetDateTime.now();
    TickJson tick = dog.newTick(); 
    SymbolMetadata metadata = smp.findMetadataForTicker("ABC.L");
    String expected = String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%s",
        metadata.getName(), metadata.getMarket(),
        tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol());
    YahooFinanceRealtime yfr = new YahooFinanceRealtime(metadata, date, FTSE100, ABC, expected);
    String actual = yfr.toString();
    assertThat(actual).isEqualTo(expected);
  }
  
}

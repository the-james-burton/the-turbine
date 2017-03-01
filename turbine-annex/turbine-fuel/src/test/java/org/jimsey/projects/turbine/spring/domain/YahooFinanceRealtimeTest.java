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

import static org.assertj.core.api.Assertions.*;
import static org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceRealtime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooFinanceRealtimeTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final DomainObjectGenerator dog = new RandomDomainObjectGenerator(Ticker.of(ABC, ABCName));
    
  @Test
  public void testRoundTripForString() {
    Ticker ticker = Ticker.of(ABC, ABCName);
    double open = 114.43;
    double high = 114.56;
    double low = 113.51;
    double close = 113.87;
    long vol = 13523517;
    String line = String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%d",
        ticker.getName().toString(), ticker.getExchangeAsString(), open, high, low, close, vol);
    logger.info(line);
    YahooFinanceRealtime yfr = YahooFinanceRealtime.of(OffsetDateTime.now(), line, ticker);

    // check the individual properties...
    assertThat(yfr.getTick().getTickerAsObject()).isEqualTo(ticker);
    assertThat(yfr.getTick().getOpen()).isEqualTo(open);
    assertThat(yfr.getTick().getHigh()).isEqualTo(high);
    assertThat(yfr.getTick().getLow()).isEqualTo(low);
    assertThat(yfr.getTick().getClose()).isEqualTo(close);
    assertThat(yfr.getTick().getVol()).isEqualTo(vol);

    // check the formatted output string...
    assertThat(yfr.toString()).isEqualTo(line);
  }
  
  @Test
  public void testRoundTripForTickJson() {
    TickJson tick = dog.newTick(); 
    Ticker ticker = Ticker.of(ABC, ABCName);
    YahooFinanceRealtime yfr = YahooFinanceRealtime.of(tick);

    // check the individual properties...
    assertThat(yfr.getTick()).isEqualTo(tick);
    assertThat(yfr.getTick().getOpen()).isEqualTo(tick.getOpen());
    assertThat(yfr.getTick().getHigh()).isEqualTo(tick.getHigh());
    assertThat(yfr.getTick().getLow()).isEqualTo(tick.getLow());
    assertThat(yfr.getTick().getClose()).isEqualTo(tick.getClose());
    assertThat(yfr.getTick().getVol()).isEqualTo(tick.getVol());

    // check the formatted output string...
    String expected = String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%d",
        ticker.getName(), ticker.getExchangeAsString(),
        tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol());
    assertThat(yfr.toString()).isEqualTo(expected);

  }
  
}

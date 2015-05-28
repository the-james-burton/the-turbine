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
package eu.verdelhan.ta4j;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jimsey.projects.turbine.spring.domain.STick;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;

public class Ta4jBasicTest {

  private static final Logger logger = LoggerFactory.getLogger(Ta4jBasicTest.class);

  List<Tick> ticks;
  
  TimeSeries series;

  @Before
  public void before() {
    
    final double variation = 3.0d;
    
    double open;
    double high;
    double low;
    double close = RandomUtils.nextDouble(99.0d, 101.0d);
    long volume = 100;
    
    ticks = new ArrayList<Tick>();
    
    for (DateTime time = DateTime.now().minusMonths(1); time.isBeforeNow(); time = time.plusDays(1)) {
      open = close;
      high = RandomUtils.nextDouble(close, close + variation);
      low = RandomUtils.nextDouble(Math.max(0, close - variation), close);
      close = RandomUtils.nextDouble(Math.max(0, low), high);
      
      Tick tick = new STick(time, open, high, low, close, RandomUtils.nextDouble(volume - variation, volume + variation));
      ticks.add(tick);
      series = new TimeSeries(ticks);
    }
  }
  
  @Test
  public void basicTa4JTest() {
    for (Tick tick : ticks) {
      // TODO Tick.toString() does not work - raise an issue in Ta4J...
      System.out.println(tick.toString());
      
    }
    
    ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
    SMAIndicator sma = new SMAIndicator(closePriceIndicator, 5);
    logger.info("SMA: {}", sma.getValue(7));
  }

}

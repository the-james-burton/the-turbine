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
package eu.verdelhan.ta4j;

import static org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingRecordTest {

  private static final Logger logger = LoggerFactory.getLogger(TradingRecordTest.class);

  DomainObjectGenerator rdog = new RandomDomainObjectGenerator(MARKET, SYMBOL);

  TimeSeries series;

  @Before
  public void before() {
    series = new TimeSeries(new ArrayList<Tick>());
  }

  @Test
  public void testTradingRecord() {
    TradingRecord tradingRecord = new TradingRecord();

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.enter(series.getEnd(), Decimal.valueOf(1), Decimal.valueOf(2));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.exit(series.getEnd(), Decimal.valueOf(3), Decimal.valueOf(4));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.enter(series.getEnd(), Decimal.valueOf(5), Decimal.valueOf(6));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.exit(series.getEnd(), Decimal.valueOf(7), Decimal.valueOf(8));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.enter(series.getEnd(), Decimal.valueOf(9), Decimal.valueOf(10));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.exit(series.getEnd(), Decimal.valueOf(11), Decimal.valueOf(12));

    moreTicks(5).stream().forEach(tick -> series.addTick(tick));
    tradingRecord.getTrades().stream().forEach((trade) -> {
      logger.info(trade.getEntry().toString());
      logger.info(trade.getExit().toString());
    });
  }

  private List<Tick> moreTicks(int number) {
    List<Tick> result = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      result.add(rdog.newTick());
      try {
        Thread.sleep(10l);
      } catch (InterruptedException e) {
        logger.warn("interrupted");
      }
    }
    return result;
  }

}

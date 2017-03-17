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

import static java.lang.String.*;
import static org.assertj.core.api.Assertions.*;
import static org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceHistoric;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaslang.Function1;
import javaslang.collection.List;
import javaslang.collection.Stream;

public class YahooFinanceHistoricTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final DomainObjectGenerator dog = new RandomDomainObjectGenerator(Ticker.of(ABC, ABCName));

  Function1<TickJson, String> convertTickToLine = (tick) -> format("%s,%.2f,%.2f,%.2f,%.2f,%d,%.2f",
      tick.getTimestampAsObject().format(DateTimeFormatter.ISO_LOCAL_DATE),
      tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol(), tick.getClose());

  @Test
  public void testRoundTripForString() {
    Ticker ticker = Ticker.of(ABC, ABCName);

    // create ten ticks...
    OffsetDateTime today = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0);
    List<TickJson> ticks = Stream.range(1, 10)
        .map(x -> dog.newTick(today.minusDays(x)))
        .toList();

    // convert these ticks to the yahoo historic format...
    String lines = ticks
        .map(tick -> convertTickToLine.apply(tick))
        .reduce((a, b) -> format("%s\n%s", a, b));

    // produce a test payload...
    String header = "Date,Open,High,Low,Close,Volume,Adj Close\n";
    String body = header.concat(lines);
    logger.info(body);

    // main event: attempt to parse the payload...
    YahooFinanceHistoric yfh = YahooFinanceHistoric.of(body, ticker);

    // check the formatted output string ...
    assertThat(yfh.toString()).isEqualTo(body);

    // check all the individual ticks are equal to the input...
    yfh.getTicks().zip(ticks)
        .forEach(tuple -> {
          TickJson tick1 = tuple._1;
          TickJson tick2 = tuple._2;
          assertThat(tick1.getTickerAsObject()).isEqualTo(tick2.getTickerAsObject());
          assertThat(tick1.getOpen()).isCloseTo(tick2.getOpen(), within(0.01));
          assertThat(tick1.getHigh()).isCloseTo(tick2.getHigh(), within(0.01));
          assertThat(tick1.getLow()).isCloseTo(tick2.getLow(), within(0.01));
          assertThat(tick1.getClose()).isCloseTo(tick2.getClose(), within(0.01));
          assertThat(tick1.getVol()).isEqualTo(tick2.getVol());
        });

  }

}

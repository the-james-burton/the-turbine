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
package org.jimsey.projects.turbine.furnace.service;

import static java.lang.String.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceRealtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javaslang.control.Try;

public class TickProducer implements Comparable<TickProducer> {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Ticker ticker;

  private final DomainObjectGenerator rdog;

  private final String baseUrl;

  private final RestTemplate rest;

  // Stream.of(body.split("\\n"))
  // .map(line -> YahooFinanceRealtime.of(metadata, OffsetDateTime.now(), line))
  // .toList()

  public TickProducer(RestTemplate rest, Ticker ticker, String baseUrl) {
    this.rest = rest;
    this.ticker = ticker;
    this.baseUrl = baseUrl;
    this.rdog = new RandomDomainObjectGenerator(ticker);
    logger.info("");
  }

  public static TickProducer of(RestTemplate rest, Ticker ticker, String baseUrl) {
    return new TickProducer(rest, ticker, baseUrl);
  }

  // TODO issue #20 use turbine-inlet
  public TickJson createTick() {
    fetchTickFromYahooFinanceRealtime();
    TickJson tick = rdog.newTick();
    return tick;
  }

  public TickJson fetchTickFromYahooFinanceRealtime() {
    String url = baseUrl + ticker.getRicAsString();
    ResponseEntity<String> response = Try.of(() -> rest.getForEntity(url, String.class))
        .getOrElse(() -> new ResponseEntity<String>(format("turbine inlet service expected at %s", url), HttpStatus.I_AM_A_TEAPOT));
    // there will only be one line, since we are not (yet) batching requests to the external finance service...
    logger.info("{} called {} and got {}:{}", toString(), url, response.getBody(), response.getStatusCode());
    YahooFinanceRealtime yfr = Try.of(() -> YahooFinanceRealtime.of(OffsetDateTime.now(), response.getBody(), ticker))
        // TODO I don't like the way the exception is lost here...
        .getOrElse(() -> {
          logger.info("unable to parse response as a YFR: {}", response.getBody());
          return null;
        });
    logger.info("yfr:{}", yfr);
    return null;
  }

  public Ticker getTicker() {
    return ticker;
  }

  // ----------------------------
  private final Comparator<TickProducer> comparator = Comparator
      .comparing(tickProducer -> tickProducer.getTicker().toString());

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof TickProducer)) {
      return false;
    }
    TickProducer that = (TickProducer) key;
    return Objects.equals(this.ticker, that.ticker);
  }

  @Override
  public int compareTo(TickProducer that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticker);
  }

  @Override
  public String toString() {
    // return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    return ticker.toString();
  }

}

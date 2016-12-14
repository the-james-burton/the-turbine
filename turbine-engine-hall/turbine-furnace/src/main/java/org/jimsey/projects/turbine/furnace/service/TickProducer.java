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

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.Objects;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class TickProducer implements Comparable<TickProducer> {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  
  private final Ticker ticker;

  private final DomainObjectGenerator rdog;

  private RestTemplate restTemplate;
  
  public TickProducer(Ticker ticker) {
    this.ticker = ticker;
    this.rdog = new RandomDomainObjectGenerator(ticker);
    logger.info("");
  }

  public static TickProducer of(Ticker ticker) {
    return new TickProducer(ticker);
  }

  // TODO issue #20 use turbine-inlet
  public TickJson createTick() {
    TickJson tick = rdog.newTick();
    return tick;
  }

  public TickJson fetchTickFromYahooFinanceRealtime() {
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

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // ----------------------------

}

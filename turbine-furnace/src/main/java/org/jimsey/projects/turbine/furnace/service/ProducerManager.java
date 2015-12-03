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
package org.jimsey.projects.turbine.furnace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.jimsey.projects.turbine.furnace.TickProducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import com.sun.istack.NotNull;

@Service
@ConfigurationProperties(prefix = "producer")
@ManagedResource
public class ProducerManager {

  private static final Logger logger = LoggerFactory.getLogger(ProducerManager.class);

  @Autowired
  @NotNull
  private CamelContext camel;

  @Autowired
  @NotNull
  private TickProducerFactory tickProducerFactory;

  /** */
  private List<TickProducer> producers = new ArrayList<>();

  @PostConstruct
  public void init() {
    for (Stocks symbol : Stocks.values()) {
      logger.info("creating TickProducer {}", symbol);
      TickProducer producer = tickProducerFactory.createTickProducer(symbol.getMarket(), symbol.getSymbol());
      producers.add(producer);
    }
  }

  @ManagedOperation
  public void produceTick(String market, String symbol) {
    producers.stream()
        .filter(producer -> {
          return market.equals(producer.getMarket()) && symbol.equals(producer.getSymbol());
        })
        .forEach(producer -> producer.produce());
  }

  @ManagedOperation
  public String showProducers() {
    Optional<String> result = producers.stream()
        .map(TickProducer::toString)
        .reduce((p1, p2) -> String.format("%s,%s", p1, p2));
    return result.toString();
  }

}

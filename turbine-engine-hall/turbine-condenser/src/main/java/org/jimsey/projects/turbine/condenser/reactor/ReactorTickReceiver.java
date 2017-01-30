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
package org.jimsey.projects.turbine.condenser.reactor;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.service.ElasticsearchService;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.TopicProcessor;

@Component
public class ReactorTickReceiver {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final TopicProcessor<String> topic = TopicProcessor.create("tickReactor", 4);

  @NotNull
  @Autowired
  private ElasticsearchService elasticsearch;

  @PostConstruct
  public void init() {

    /*
     * This is where the stream processing happens.
     * Add more streams with topic for more subscribers...
     */
    topic
        .doOnNext(msg -> logger.info(" reactor -> tick:{}", msg))
        .map(msg -> TickJson.of(msg))
        // .doOnNext(tick -> elasticsearch.indexTick(tick))
        .subscribe(new ReactorTickSubscriber("tickSubscriber"));

    // camel destinations...
    // ssm:///topic/ticks
    // elasticsearch://elasticsearch?ip=localhost&port=9300&operation=INDEX&indexName=turbine-ticks&indexType=turbine-tick

    // ssm:///topic/indicators
    // elasticsearch://elasticsearch?ip=localhost&port=9300&operation=INDEX&indexName=turbine-indicators&indexType=turbine-indicator

    // ssm:///topic/strategies
    // elasticsearch://elasticsearch?ip=localhost&port=9300&operation=INDEX&indexName=turbine-strategies&indexType=turbine-strategy
  }

  /**
   * TODO should the raw processor be exposed?
   * @return the reactor processor used to handle tick messages
   */
  public TopicProcessor<String> getTopic() {
    return topic;
  }

  public ElasticsearchService getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchService elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

}

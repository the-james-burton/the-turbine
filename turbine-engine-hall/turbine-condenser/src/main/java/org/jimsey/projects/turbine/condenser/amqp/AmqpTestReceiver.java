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
package org.jimsey.projects.turbine.condenser.amqp;

import static java.lang.String.*;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;

import org.jimsey.projects.turbine.condenser.reactor.LoggingSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import reactor.core.publisher.TopicProcessor;

/**
 * See AmqpSetup for details of how this class is wired up to RabbitMQ
 * @author the-james-burton
 */
@Component
public class AmqpTestReceiver {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private TopicProcessor<String> topic = TopicProcessor.create("testTopic", 4);

  @PostConstruct
  public void init() {

    /* This is where the stream processing happens.
     * Add more streams with topic for more subscribers... 
    */
    topic
    .map(s -> format("mutated! %s", s))
    .subscribe(new LoggingSubscriber<String>("testSubscriber"));
  
    topic
    .map(s -> format("mutated2! %s", s))
    .subscribe(new LoggingSubscriber<String>("testSubscriber2"));
  
    // WARNING: the TopicProcessor doesn't call onSubcribe() before returning!
    // so we must wait for a bit until we think the subscription is injected into our subscriber
    // alternatively, the call to request() can be put into onSubscribe() which is what I have done
    // although I'm not sure if that is best practice...
    //Try.run(() -> Thread.sleep(100));
    //testSubscriber.request(Long.MAX_VALUE); // no backpressure... consume as fast as possible
  }
  
  /**
   * handle RabbitMQ setup test messages
   * @param the inbound message from RabbitMQ as a String
   */
  @RabbitListener(queues = "#{queueTest}")
  public void handleMessage(String message) {
    logger.info(" ...> Spring AMQP received [{}]", message);
    
    // introduce the message to reactor...
    topic.onNext(message);
  }

}

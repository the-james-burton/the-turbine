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

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javaslang.collection.Stream;
import javaslang.control.Try;

/**
 * See AmqpSetup for details of how this class is wired up to RabbitMQ
 * @author the-james-burton
 */
@Component
@ManagedResource
public class AmqpTestSender {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RabbitTemplate rabbitTemplate;

  public AmqpTestSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  /**
   * TESTING ONLY
   */
  @ManagedOperation
  public void sendMessages(int n) {
    Stream.range(0, n)
        .forEach(x -> Try.run(() -> sendMessage()).orElseRun((e) -> logger.info("could not send AMQP message:{}", e)));
  }

  /**
   * TESTING ONLY
   */
  @ManagedOperation
  public void sendMessage() throws Exception {
    String text = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    logger.info(" ...> Spring AMQP sending message [{}]", text);
    // the defaults will do the same as below, this is really to show how to do it post-camel...
    Message message = MessageBuilder.withBody(text.getBytes())
        .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
        .build();
    rabbitTemplate.send(AmqpSetup.exchangeTestName, "", message);

  }
}

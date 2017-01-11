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

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpSetup {

  final static String queueName = "spring-boot";

  @Bean
  Queue queue() {
      return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
      return new TopicExchange("spring-boot-exchange");
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
      return BindingBuilder.bind(queue).to(exchange).with(queueName);
  }

  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
          MessageListenerAdapter listenerAdapter) {
      SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
      container.setConnectionFactory(connectionFactory);
      container.setQueueNames(queueName);
      container.setMessageListener(listenerAdapter);
      return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(ExampleReceiver receiver) {
      return new MessageListenerAdapter(receiver, "receiveMessage");
  }
}

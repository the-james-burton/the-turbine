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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class controls the wiring within RabbitMQ for our Spring AMQP implementation 
 * @author the-james-burton
 */
@Configuration
@EnableRabbit
public class AmqpSetup extends BaseConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  final static String queueTestName = "turbine.test.queue";

  final static String exchangeTestName = "turbine.test.exchange";

  private String exchangeTicksName, queueTicksName;

  private CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

  @Autowired
  private AmqpAdmin rabbit;

  @PostConstruct
  public void init() {
    exchangeTicksName = infrastructureProperties.getAmqpTicksExchange();
    queueTicksName = infrastructureProperties.getAmqpTicksQueue();

    // TODO maybe the direct programming approach is better than all the beans below..?
    // rabbit.declareBinding(bindingTest());
  }

  /**
   * enables support for @RabbitListener
   * @param configurer
   * @return
   */
  @Bean
  public SimpleRabbitListenerContainerFactory myFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    // factory.setMessageConverter(myMessageConverter());
    return factory;
  }

  /**
   * 
   * @param connectionFactory injected by Spring
   * @param listenerAdapter injected by Spring
   * @return 
   */
  // @Bean
  // SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
  // MessageListenerAdapter listenerAdapterTest) {
  // SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
  // container.setConnectionFactory(connectionFactory);
  // container.setQueueNames(queueTestName);
  // container.setMessageListener(listenerAdapterTest);
  // return container;
  // }

  @Bean
  TopicExchange exchangeTest() {
    return new TopicExchange(exchangeTestName, false, true);
  }

  @Bean
  TopicExchange exchangeTicks() {
    return new TopicExchange(exchangeTicksName, true, true);
  }

  @Bean
  Queue queueTest() {
    return new Queue(queueTestName, false, false, true);
  }

  @Bean
  Queue queueTicks() {
    return new Queue(queueTicksName, false, false, true);
  }

  @Bean
  Binding bindingTest(Queue queueTest, TopicExchange exchangeTest) {
    return BindingBuilder.bind(queueTest).to(exchangeTest).with("");
  }

  @Bean
  Binding bindingTicks(Queue queueTicks, TopicExchange exchangeTicks) {
    return BindingBuilder.bind(queueTicks).to(exchangeTicks).with("");
  }

  // @Bean
  // MessageListenerAdapter listenerAdapterTest(TestReceiver receiver) {
  // return new MessageListenerAdapter(receiver);
  // }
  //
  // @Bean
  // MessageListenerAdapter listenerAdapterTicks(TickReceiver receiver) {
  // return new MessageListenerAdapter(receiver);
  // }

  public AmqpAdmin getRabbit() {
    return rabbit;
  }

  public void setRabbit(AmqpAdmin rabbit) {
    this.rabbit = rabbit;
  }
}

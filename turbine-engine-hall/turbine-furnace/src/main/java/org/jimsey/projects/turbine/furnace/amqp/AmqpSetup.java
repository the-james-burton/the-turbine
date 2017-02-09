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
package org.jimsey.projects.turbine.furnace.amqp;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.furnace.component.InfrastructureProperties;
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
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
public class AmqpSetup {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  private static String exchangeTicksName, queueTicksName;

  private ConnectionFactory connectionFactory = new CachingConnectionFactory();

  @Autowired
  private AmqpAdmin rabbit;

  @PostConstruct
  public void init() {
    exchangeTicksName = getInfrastructureProperties().getAmqpTicksExchange();
    queueTicksName = getInfrastructureProperties().getAmqpTicksQueue();

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

  @Bean
  TopicExchange exchangeTicks() {
    return new TopicExchange(exchangeTicksName, true, true);
  }

  @Bean
  Queue queueTicks() {
    return new Queue(queueTicksName, false, false, true);
  }

  @Bean
  Binding bindingTicks(Queue queueTicks, TopicExchange exchangeTicks) {
    return BindingBuilder.bind(queueTicks).to(exchangeTicks).with("");
  }

  public AmqpAdmin getRabbit() {
    return rabbit;
  }

  public void setRabbit(AmqpAdmin rabbit) {
    this.rabbit = rabbit;
  }

  public InfrastructureProperties getInfrastructureProperties() {
    return infrastructureProperties;
  }

  public void setInfrastructureProperties(InfrastructureProperties infrastructureProperties) {
    this.infrastructureProperties = infrastructureProperties;
  }

  public static String getExchangeTicksName() {
    return exchangeTicksName;
  }

  public static String getQueueTicksName() {
    return queueTicksName;
  }

}

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
package org.jimsey.projects.turbine.furnace.component;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;

@Component
@ConfigurationProperties(prefix = "infrastructure")
public class InfrastructureProperties {

  private static final Logger logger = LoggerFactory.getLogger(InfrastructureProperties.class);

  @NotNull
  private String environmentName;

  @NotNull
  private String amqpServer;

  @NotNull
  private String amqpCamelComponent;

  @NotNull
  private String amqpTicksExchange;

  @NotNull
  private String amqpTicksQueue;

  @PostConstruct
  public void init() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    logger.info(objectMapper.writeValueAsString(this));
  }

  // ------------------------------------------
  public String getEnvironmentName() {
    return environmentName;
  }

  public void setEnvironmentName(String environmentName) {
    this.environmentName = environmentName;
  }

  public String getAmqpServer() {
    return amqpServer;
  }

  public void setAmqpServer(String amqpServer) {
    this.amqpServer = amqpServer;
  }

  public String getAmqpTicksExchange() {
    return amqpTicksExchange;
  }

  public void setAmqpTicksExchange(String amqpTicksExchange) {
    this.amqpTicksExchange = amqpTicksExchange;
  }

  public String getAmqpTicksQueue() {
    return amqpTicksQueue;
  }

  public void setAmqpTicksQueue(String amqpTicksQueue) {
    this.amqpTicksQueue = amqpTicksQueue;
  }

  public String getAmqpCamelComponent() {
    return amqpCamelComponent;
  }

  public void setAmqpCamelComponent(String amqpCamelComponent) {
    this.amqpCamelComponent = amqpCamelComponent;
  }

}

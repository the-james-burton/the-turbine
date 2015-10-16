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
package org.jimsey.projects.turbine.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  StompEndpointRegistry registry;

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry config) {
    // to support stomp over websockets natively...
    // config.enableSimpleBroker("/topic");

    // to use the stomp support build into RabbitMQ...
    config.enableStompBrokerRelay("/topic").setRelayHost("localhost").setRelayPort(61613);

    config.setApplicationDestinationPrefixes("/app");
    config.setPathMatcher(new AntPathMatcher("."));
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    this.registry = registry;
    registry.addEndpoint("/reply").setAllowedOrigins("http://localhost:9000").withSockJS();
    registry.addEndpoint("/ping").setAllowedOrigins("http://localhost:9000").withSockJS();
    registry.addEndpoint("/ticks").setAllowedOrigins("http://localhost:9000").withSockJS();
  }

  // TODO add stomp endpoints for multiple markets/symbols?

}
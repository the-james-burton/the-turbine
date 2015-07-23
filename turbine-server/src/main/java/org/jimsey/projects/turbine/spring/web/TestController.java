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
package org.jimsey.projects.turbine.spring.web;

import java.util.Date;
import java.util.Map;

import org.jimsey.projects.turbine.spring.service.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

  private static final Logger logger = LoggerFactory.getLogger(TestController.class);

  @Autowired
  private Ping ping;

  @RequestMapping("/test")
  public String welcome(Map<String, Object> model) {
    model.put("time", new Date());
    model.put("message", "Freemarker is working");
    return "test";
  }

  @MessageMapping("/ping")
  @SendTo("/topic/ping")
  public PingResponse ping() throws Exception {
    return new PingResponse(ping.ping());
  }
  
  @MessageMapping("/reply")
  @SendTo("/topic/reply")
  public ReplyResponse reply(ReplyResponse message) throws Exception {
    logger.debug("received message={}", message.getMessage());
    ReplyResponse response = new ReplyResponse();
    response.setMessage(String.format("Hello %s, the time here is %s", message.getMessage(), ping.ping()));
    return response;
  }

}
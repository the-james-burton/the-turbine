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

import java.time.OffsetDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.exceptions.TurbineException;
import org.jimsey.projects.turbine.spring.service.ElasticsearchService;
import org.jimsey.projects.turbine.spring.service.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

@RestController
@EnableAutoConfiguration
@RequestMapping(TurbineConstants.REST_ROOT_TICKS)
public class TickController {

  private static final Logger logger = LoggerFactory.getLogger(TickController.class);

  private static ObjectMapper json = new ObjectMapper();

  @Autowired
  @NotNull
  ElasticsearchService elasticsearch;

  @Autowired
  private Ping ping;

  @PostConstruct
  public void init() throws Exception {
    long sod = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).toInstant().toEpochMilli();
    long now = OffsetDateTime.now().toInstant().toEpochMilli();
    logger.info("right now date value is {}", now);;
    logger.info("this mornings date value is {}", sod);
    logger.info("this mornings getTicksAfter() : [{}]", getTicksAfter("ABC", sod));
  }

  @RequestMapping("/ping")
  public PingResponse ping() throws Exception {
    logger.info("ping()");
    return new PingResponse(ping.ping());
  }

  @RequestMapping("/{symbol}")
  public String getTicks(@PathVariable String symbol) {
    logger.info("getTicks");
    List<TickJson> ticks = elasticsearch.findBySymbol(symbol);
    return Joiner.on(',').join(ticks);
  }

  @RequestMapping("/{symbol}/{date}")
  public String getTicksAfter(@PathVariable String symbol, @PathVariable Long date) throws JsonProcessingException {
    List<TickJson> ticks = elasticsearch.findBySymbolAndDateGreaterThan(symbol, date);
    //return Joiner.on(',').join(ticks);
    //return objectToJson(ticks);
    //if (true) {
    // throw new Exception("I hate you!");
    //}
    
    // TODO can do this with java 8 lambdas?
    Object ticksDTO = new Object() {
      @JsonProperty("ticks")
      List<TickJson> tickz = ticks;
//      List<TickJson> getTicks() {
//        return
//      }
    };
    return json.writeValueAsString(ticksDTO);
  }

  /**
   * No need for this. Spring boot will send a decent error message to the client.
   * @param ticks
   * @return
   */
  @Deprecated
  private String objectToJson(List<TickJson> ticks) {
    String result = null;
    try {
      result = json.writeValueAsString(ticks);
    } catch (JsonProcessingException e) {
      try {
        logger.error(e.getMessage(), e);
        result = json.writeValueAsString(new TurbineException(e.getMessage()));
      } catch (Exception x) {
        logger.error(x.getMessage(), x);
      }
    }
    return StringUtils.defaultString(result);
  }

}
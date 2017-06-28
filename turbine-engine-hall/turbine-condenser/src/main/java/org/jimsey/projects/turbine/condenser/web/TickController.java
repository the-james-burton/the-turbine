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
package org.jimsey.projects.turbine.condenser.web;

import java.time.OffsetDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.condenser.exceptions.TurbineException;
import org.jimsey.projects.turbine.condenser.service.ElasticsearchService;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.fuel.constants.TurbineFuelConstants;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import io.vavr.control.Option;

@RestController
// @EnableAutoConfiguration
@RequestMapping(TurbineCondenserConstants.REST_ROOT_TICKS)
public class TickController {

  private static final Logger logger = LoggerFactory.getLogger(TickController.class);

  private static ObjectMapper json = new ObjectMapper();

  @NotNull
  @Autowired
  // @Qualifier("elasticsearchServiceJestImpl")
  // @Resource(name = "elasticsearchNativeServiceImpl")
  ElasticsearchService elasticsearch;

  @Autowired
  private Ping ping;

  @PostConstruct
  public void init() throws Exception {
    long sod = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant().toEpochMilli();
    long now = OffsetDateTime.now().toInstant().toEpochMilli();
    logger.info("right now date value is {}", now);
    logger.info("this mornings date value is {}", sod);
    String ric = TurbineFuelConstants.tickerA.getRicAsString();
    Option<String> ticks = Option.of(getTicks(ric));
    logger.info("*** getTicks({}) : [{}]", ric, ticks.getOrElse("").split("timestamp").length - 1);
    // logger.info("this mornings getTicksAfter(ABC) : [{}]", getTicksAfter("ABC", sod));
  }

  @RequestMapping("/ping")
  public PingResponse ping() throws Exception {
    logger.info("ping()");
    return new PingResponse(ping.ping());
  }

  @RequestMapping("/{ric}")
  public String getTicks(
      @PathVariable String ric) {
    logger.info("getTicks({})", ric);
    List<TickJson> ticks = elasticsearch.findTicksByRic(ric);
    if (ticks == null) {
      return null;
    }
    return Joiner.on(',').useForNull("").join(ticks);
  }

  @RequestMapping("/{ric}/{date}")
  public String getTicksAfter(
      @PathVariable String ric,
      @PathVariable Long date) throws JsonProcessingException {
    logger.info("getTicksAfter({}, {}, {})", ric, date);
    List<TickJson> ticks = elasticsearch.findTicksByRicAndDateGreaterThan(ric, date);
    // return Joiner.on(',').join(ticks);
    // return objectToJson(ticks);
    // if (true) {
    // throw new Exception("I hate you!");
    // }

    // TODO can do this with java 8 lambdas?
    Object dto = new Object() {
      @JsonProperty("ticks")
      List<TickJson> tickz = ticks;
      // List<TickJson> getTicks() {
      // return
      // }
    };
    return json.writeValueAsString(dto);
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
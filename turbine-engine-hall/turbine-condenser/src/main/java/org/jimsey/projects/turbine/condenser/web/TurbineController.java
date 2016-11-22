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

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.condenser.service.TurbineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
// @EnableAutoConfiguration
@RequestMapping(TurbineCondenserConstants.REST_ROOT_TURBINE)
public class TurbineController {

  private static final Logger logger = LoggerFactory.getLogger(TurbineController.class);

  private static ObjectMapper json = new ObjectMapper();

  @Autowired
  @NotNull
  private Ping ping;

  @Autowired
  @NotNull
  private TurbineService turbineService;

  @PostConstruct
  public void init() throws Exception {
    logger.info("TurbineController: initialised");
  }

  /*
   * ...with spring security, atacama fails when @CrossOrigin is used with
   * No 'Access-Control-Allow-Origin' header is present on the requested resource.
   * Origin 'http://localhost:3000' is therefore not allowed access.
   */

  @RequestMapping(path = "/ping", produces = "application/json")
  // @CrossOrigin(origins = "*", maxAge = 3600)
  public PingResponse ping() throws Exception {
    logger.info("ping()");
    return new PingResponse(ping.ping());
  }

  @RequestMapping("/stocks/{market}")
  // @CrossOrigin(origins = "*", maxAge = 3600)
  public String stocks(@PathVariable String market) throws Exception {
    logger.info("stocks({})", market);
    return turbineService.listStocks(market);
  }

  @RequestMapping("/indicators")
  // @CrossOrigin(origins = "*", maxAge = 3600)
  public String indicators() throws Exception {
    logger.info("indicators()");
    return turbineService.listIndicators();
  }

  @RequestMapping("/strategies")
  // @CrossOrigin(origins = "*", maxAge = 3600)
  public String strategies() throws Exception {
    logger.info("strategies()");
    return turbineService.listStrategies();
  }

}

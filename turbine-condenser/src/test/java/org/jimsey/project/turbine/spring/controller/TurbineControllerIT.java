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
package org.jimsey.project.turbine.spring.controller;

import static org.junit.Assert.*;

import java.lang.invoke.MethodHandles;
import java.net.URL;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.Application;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.service.Stocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=48002" })
@ActiveProfiles("it")
public class TurbineControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

  @Value("${local.server.port}")
  private int port;

  private URL serviceUrl;

  private RestTemplate template;

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  @Before
  public void setUp() throws Exception {
    this.serviceUrl = new URL(String.format("http://localhost:%s/turbine/", port));
    template = new TestRestTemplate();
  }

  @Test
  public void testPing() throws Exception {
    logger.info("it should return nano time value...");
    String url = String.format("%s/%s", serviceUrl.toString(), "ping");
    ResponseEntity<String> response = template.getForEntity(url, String.class);
    logger.info("ping: {}", response.getBody());
    assertNotNull(response.getBody());
  }

  @Test
  public void testListSymbols() throws Exception {
    logger.info("it should return a list of symbols...");
    String url = String.format("%s/%s/%s", serviceUrl.toString(), "stocks", Stocks.ABC.getMarket());
    ResponseEntity<String> response = template.getForEntity(url, String.class);
    logger.info("symbols: {}", response.getBody());
    assertNotNull(response.getBody());
  }

  @Test
  public void testListIndicators() throws Exception {
    logger.info("it should return a list of indicators...");
    String url = String.format("%s/%s", serviceUrl.toString(), "indicators");
    ResponseEntity<String> response = template.getForEntity(url, String.class);
    logger.info("indicators: {}", response.getBody());
    assertNotNull(response.getBody());
  }

  @Test
  public void testListStrategies() throws Exception {
    logger.info("it should return a list of strategies...");
    String url = String.format("%s/%s", serviceUrl.toString(), "strategies");
    ResponseEntity<String> response = template.getForEntity(url, String.class);
    logger.info("strategies: {}", response.getBody());
    assertNotNull(response.getBody());
  }

}
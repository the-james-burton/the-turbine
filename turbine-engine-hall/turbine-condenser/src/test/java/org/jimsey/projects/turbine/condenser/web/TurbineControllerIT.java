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

import static org.assertj.core.api.Assertions.*;

import java.lang.invoke.MethodHandles;

import org.jimsey.projects.turbine.fuel.domain.Stocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public class TurbineControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

  private final String path = "/turbine";

  @Autowired
  private TestRestTemplate rest;

  @Before
  public void setUp() throws Exception {
    logger.info("given a full running system, authenticated");
    rest = rest.withBasicAuth("user", "password");
  }

  @Test
  public void testPing() throws Exception {
    String url = String.format("%s/%s", path, "ping");
    logger.info("when {} is called...", url);
    ResponseEntity<String> response = rest.getForEntity(url, String.class);
    logger.info("ping: {}", response.getBody());
    logger.info("then it should return nano time value...");
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testListSymbols() throws Exception {
    String url = String.format("%s/%s/%s", path, "stocks", Stocks.ABC.getMarket());
    logger.info("when {} is called...", url);
    ResponseEntity<String> response = rest.getForEntity(url, String.class);
    logger.info("symbols: {}", response.getBody());
    logger.info("then it should return a list of symbols...");
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testListIndicators() throws Exception {
    String url = String.format("%s/%s", path, "indicators");
    logger.info("when {} is called...", url);
    ResponseEntity<String> response = rest.getForEntity(url, String.class);
    logger.info("indicators: {}", response.getBody());
    logger.info("then it should return a list of indicators...");
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testListStrategies() throws Exception {
    String url = String.format("%s/%s", path, "strategies");
    logger.info("when {} is called...", url);
    ResponseEntity<String> response = rest.getForEntity(url, String.class);
    logger.info("strategies: {}", response.getBody());
    logger.info("then it should return a list of strategies...");
    assertThat(response.getBody()).isNotNull();
  }

}
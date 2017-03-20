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
package org.jimsey.projects.turbine.inlet.web;

import static java.lang.String.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;

import org.assertj.core.util.Arrays;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceHistoric;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceRealtime;
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
import org.springframework.test.context.junit4.SpringRunner;

import javaslang.collection.List;

/**
 * There appears to be no easy way to use a real bean in a @WebMvcTest.
 * This means that a 'real' DogKennel' instance cannot be given to the FinanceController.
 * It is not practical to mock the entire DogKennel,
 * therefore this test is @SpringBootTest and not @WebMvcTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public class FinanceControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String historicUrl = "/finance/yahoo/historic/%s";
  private static final String realtimeUrl = "/finance/yahoo/realtime/%s";

  private final Ticker TEST1_L = Ticker.of(".L");
  private final Ticker TEST2_L = Ticker.of("TEST2.L");
  private final Ticker TEST3_L = Ticker.of("TEST3.L");

  @Autowired
  private TestRestTemplate rest;

  @Test
  public void testPingAsString() throws Exception {
    logger.info("when /test/ping is called");
    ResponseEntity<String> response = rest.getForEntity("/test/ping", String.class);
    logger.info("then the response body should not be null [body:{}]", response.getBody());
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testYahooFinanceHistoricOne() throws Exception {
    String body = doRestQuery(historicUrl, TEST1_L.getRicAsString());
    logger.info("and the response body should be parseable as a YahooFinanceHistoric object");
    YahooFinanceHistoric yfr = YahooFinanceHistoric.of(body, TEST1_L);
    assertThat(yfr.getTicks()).hasSize(10);
  }

  @Test
  public void testYahooFinanceRealtimeOne() throws Exception {
    String body = doRestQuery(realtimeUrl, TEST1_L.getRicAsString());
    logger.info("and the response body should be parseable as a YahooFinanceRealtime object");
    YahooFinanceRealtime yfr = YahooFinanceRealtime.of(OffsetDateTime.now(), body, TEST1_L);
    assertThat(yfr.getTick()).isNotNull();
  }

  @Test
  public void testYahooFinanceRealtimeTwo() throws Exception {
    String body = doRestQuery(realtimeUrl, format("%s+%s", TEST1_L.getRicAsString(), TEST2_L.getRicAsString()));
    logger.info("and the response body should be parseable as a YahooFinanceRealtime object");
    List<YahooFinanceRealtime> yfrs = YahooFinanceRealtime.of(OffsetDateTime.now(), body.split("\\n"),
        Arrays.array(TEST1_L, TEST2_L));
    assertThat(yfrs).hasSize(2);
  }

  private String doRestQuery(String url, String queryString) {
    String uri = format(realtimeUrl, queryString);
    logger.info(format("when %s is called", uri));
    ResponseEntity<String> response = rest.getForEntity(uri, String.class);
    String body = response.getBody();
    logger.info("then the response body should not be null [body:{}]", response.getBody());
    assertThat(body).isNotNull();
    assertThat(body).isNotEmpty();
    return body;
  }

}
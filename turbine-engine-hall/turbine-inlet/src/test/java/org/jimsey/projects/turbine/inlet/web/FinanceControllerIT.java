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

import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.inlet.domain.TickerMetadata;
import org.jimsey.projects.turbine.inlet.domain.TickerMetadataProvider;
import org.jimsey.projects.turbine.inlet.domain.YahooFinanceRealtime;
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
import org.springframework.test.context.junit4.SpringRunner;

import javaslang.collection.List;
import javaslang.collection.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
public class FinanceControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Ticker TEST1_L = Ticker.of("TEST1.L");
  
  private final Ticker TEST2_L = Ticker.of("TEST2.L");
  
  private final Ticker TEST3_L = Ticker.of("TEST3.L");
  
  @Autowired
  private TickerMetadataProvider tickerMetadataProvider;
  
  @Autowired
  private TestRestTemplate rest;

  @Before
  public void setUp() throws Exception {
    tickerMetadataProvider.addMetadata(TickerMetadata.of(TEST1_L, "TEST1Name"));
    tickerMetadataProvider.addMetadata(TickerMetadata.of(TEST2_L, "TEST2Name"));
    tickerMetadataProvider.addMetadata(TickerMetadata.of(TEST3_L, "TEST3Name"));
  }

  @Test
  public void testPingAsString() throws Exception {
    logger.info("when /test/ping is called");
    ResponseEntity<String> response = rest.getForEntity("/test/ping", String.class);
    logger.info("then the response body should not be null [body:{}]", response.getBody());
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testYahooFinanceRealtimeOne() throws Exception {
    String body = doRestQuery(TEST1_L.toString());
    logger.info("and the response body should be parseable as a YahooFinanceRealtime object");
    List<YahooFinanceRealtime> yfrs = parseBody(body);
    assertThat(yfrs).hasSize(1);
  }

  @Test
  public void testYahooFinanceRealtimeTwo() throws Exception {
    String body = doRestQuery(format("%s+%s", TEST1_L, TEST2_L));
    logger.info("and the response body should be parseable as a YahooFinanceRealtime object");
    List<YahooFinanceRealtime> yfrs = parseBody(body);
    assertThat(yfrs).hasSize(2);
  }

  private List<YahooFinanceRealtime> parseBody(String body) {
    return Stream.of(body.split("\\n"))
        .map(line -> YahooFinanceRealtime.of(TickerMetadata.of(TEST1_L, "TEST1Name"), OffsetDateTime.now(), line))
        .toList();
  }
  
  private String doRestQuery(String queryString) {
    String uri = format("/finance/yahoo/realtime/%s", queryString);
    logger.info(format("when %s is called", uri));
    ResponseEntity<String> response = rest.getForEntity(uri, String.class);
    String body = response.getBody();
    logger.info("then the response body should not be null [body:{}]", response.getBody());
    assertThat(body).isNotNull();
    assertThat(body).isNotEmpty();
    return body;
  }
  
}
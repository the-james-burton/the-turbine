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
package org.jimsey.projects.turbine.condenser.web;

import static org.hamcrest.Matchers.*;
import static org.jimsey.projects.turbine.fuel.constants.TurbineTestConstants.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.condenser.service.ElasticsearchService;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(IndicatorController.class)
@ActiveProfiles("it")
public class IndicatorControllerTest {

  @MockBean
  private ElasticsearchService elasticsearch;

  @MockBean
  private Ping ping;
  
  @Autowired
  private MockMvc mvc;

  private DomainObjectGenerator rdog = new RandomDomainObjectGenerator(MARKET, SYMBOL);

  private List<IndicatorJson> indicators = new ArrayList<IndicatorJson>();

  private static ObjectMapper json = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    indicators = new ArrayList<IndicatorJson>();
    TickJson tick = rdog.newTick();
    indicators.add(rdog.newIndicator(tick.getTimestampAsObject(), "testName"));
  }

  @Test
  public void testGetAllStocksGreaterThanDate() throws Exception {
    given(elasticsearch
        .findIndicatorsByMarketAndSymbolAndNameAndDateGreaterThan(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Long.class)))
        .willReturn(indicators);

    String expected = json.writeValueAsString(new Object() {
      @JsonProperty("indicators")
      List<IndicatorJson> indicatorz = indicators;
    });

    long date = Instant.now().minus(1, ChronoUnit.MINUTES).toEpochMilli();

    String restUri = String.format("%s/%s/%s/%s/%s",
        TurbineCondenserConstants.REST_ROOT_INDICATORS, "market", "symbol", "testName", date);

    mvc.perform(get(restUri).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(expected)));
  }
}
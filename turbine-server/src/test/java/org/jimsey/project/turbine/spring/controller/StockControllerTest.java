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

// import static org.hamcrest.Matchers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jimsey.project.turbine.spring.TurbineTestConstants;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.domain.StockJson;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.service.DomainObjectGenerator;
import org.jimsey.projects.turbine.spring.service.ElasticsearchService;
import org.jimsey.projects.turbine.spring.service.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.spring.web.StockController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class StockControllerTest {

  @InjectMocks
  private StockController controller;

  // @Spy
  @Mock
  private ElasticsearchService elasticsearch;

  private MockMvc mvc;

  private DomainObjectGenerator rdog = new RandomDomainObjectGenerator(
      TurbineTestConstants.MARKET, TurbineTestConstants.SYMBOL);

  private List<StockJson> stocks = new ArrayList<StockJson>();

  private static ObjectMapper json = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    stocks = new ArrayList<StockJson>();
    TickJson tick = rdog.newTick();
    stocks.add(rdog.newStock(tick.getTimestampAsObject()));
  }

  @Test
  public void testGetAllStocksGreaterThanDate() throws Exception {
    Mockito.when(elasticsearch
        .findStocksBySymbolAndDateGreaterThan(Mockito.anyString(), Mockito.any(Long.class)))
        .thenReturn(stocks);

    String expected = json.writeValueAsString(new Object() {
      @JsonProperty("stocks")
      List<StockJson> stockz = stocks;
    });

    long date = Instant.now().minus(1, ChronoUnit.MINUTES).toEpochMilli();

    String restUri = String.format("%s/all/%s", TurbineConstants.REST_ROOT_STOCKS, date);

    mvc.perform(MockMvcRequestBuilders.get(restUri).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(expected)));
  }
}
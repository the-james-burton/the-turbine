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

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.condenser.service.Stocks;
import org.jimsey.projects.turbine.condenser.service.TurbineService;
import org.jimsey.projects.turbine.condenser.web.PingResponse;
import org.jimsey.projects.turbine.condenser.web.TurbineController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class TurbineControllerTest {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @InjectMocks
  private TurbineController controller;

  @Mock
  private TurbineService turbineSerivce;

  // @Spy
  @Mock
  private Ping ping;

  private final long pingTime = 123l;

  private MockMvc mvc;

  private final List<String> indicators = Lists.newArrayList("indicator1", "indicator2");

  private final List<String> strategies = Lists.newArrayList("strategy1", "strategy1");

  private final List<Stocks> stocks = Lists.newArrayList(Stocks.ABC, Stocks.DEF);

  private final ObjectMapper json = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    logger.info(" *** ");
  }

  @Test
  public void getPing() throws Exception {
    // use this for a spy...
    // Mockito.doReturn(123l).when(ping).ping();
    when(ping.ping()).thenReturn(pingTime);
    PingResponse expected = new PingResponse(pingTime);

    mvc.perform(MockMvcRequestBuilders
        .get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "ping"))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(json.writeValueAsString(expected)));
    // .andExpect(content().string(equalTo("123")));
  }

  @Test
  public void testGetSymbols() throws Exception {
    String response = json.writeValueAsString(stocks);
    logger.info("mock response : {}", response);
    when(turbineSerivce.listStocks(anyString())).thenReturn(response);

    mvc.perform(MockMvcRequestBuilders
        .get(String.format("%s/%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "stocks", Stocks.ABC.getMarket()))
        .accept(MediaType.APPLICATION_JSON))
        .andDo(handler -> logger.info("{}", handler.getResponse().getContentAsString().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Stocks.ABC.getMarket())));

    // TODO add test for stocks not in given market...
  }

  @Test
  public void testGetIndicators() throws Exception {
    String response = json.writeValueAsString(indicators);
    logger.info("mock response : {}", response);
    when(turbineSerivce.listIndicators()).thenReturn(response);

    mvc.perform(MockMvcRequestBuilders
        .get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "indicators"))
        .accept(MediaType.APPLICATION_JSON))
        .andDo(handler -> logger.info("{}", handler.getResponse().getContentAsString().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(indicators.get(0))));
  }

  @Test
  public void testGetStrategies() throws Exception {
    String response = json.writeValueAsString(strategies);
    logger.info("mock response : {}", response);
    when(turbineSerivce.listStrategies()).thenReturn(response);

    mvc.perform(MockMvcRequestBuilders
        .get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "strategies"))
        .accept(MediaType.APPLICATION_JSON))
        .andDo(handler -> logger.info("{}", handler.getResponse().getContentAsString().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(strategies.get(0))));
  }

}
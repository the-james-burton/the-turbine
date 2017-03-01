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

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import org.jimsey.projects.turbine.condenser.TurbineCondenserConstants;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.condenser.service.TickerManager;
import org.jimsey.projects.turbine.condenser.service.TurbineService;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.inspector.constants.TurbineTestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import javaslang.collection.HashSet;
import javaslang.collection.Set;

@RunWith(SpringRunner.class)
@WebMvcTest(TurbineController.class)
@ActiveProfiles("it")
public class TurbineControllerTest {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @MockBean
  private TurbineService turbineSerivce;

  @MockBean
  private Ping ping;

  @MockBean
  private TickerManager tickerManager;

  @Autowired
  private MockMvc mvc;

  private final long pingTime = 123l;

  private final List<String> indicators = Arrays.asList("indicator1", "indicator2");

  private final List<String> strategies = Arrays.asList("strategy1", "strategy1");

  private final ObjectMapper json = new ObjectMapper();

  private Set<Ticker> tickers;

  @Before
  public void setUp() throws Exception {
    // logger.info(logger.getName());
    logger.debug(" ===========> debug log");
    logger.info(" *** ");
    tickers = HashSet.of(Ticker.of("ABC.L"), Ticker.of("DEF.L"));
  }

  @Test
  public void getPing() throws Exception {
    given(ping.ping()).willReturn(pingTime);
    given(tickerManager.getTickers()).willReturn(tickers);
    PingResponse expected = new PingResponse(pingTime);

    mvc.perform(
        get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "ping"))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(json.writeValueAsString(expected)));
    // .andExpect(content().string(equalTo("123")));
  }

  @Test
  public void testGetSymbols() throws Exception {
    String response = json.writeValueAsString(tickers.toJavaList());
    logger.info("mock response : {}", response);
    when(turbineSerivce.listStocks(anyString())).thenReturn(response);

    mvc.perform(
        get(String.format("%s/%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "stocks", TurbineTestConstants.LSE))
            .accept(MediaType.APPLICATION_JSON))
        .andDo(handler -> logger.info("{}", handler.getResponse().getContentAsString().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(TurbineTestConstants.LSE)));

    // TODO add test for stocks not in given exchange...
  }

  @Test
  public void testGetIndicators() throws Exception {
    String response = json.writeValueAsString(indicators);
    logger.info("mock response : {}", response);
    when(turbineSerivce.listIndicators()).thenReturn(response);

    mvc.perform(
        get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "indicators"))
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

    mvc.perform(
        get(String.format("%s/%s", TurbineCondenserConstants.REST_ROOT_TURBINE, "strategies"))
            .accept(MediaType.APPLICATION_JSON))
        .andDo(handler -> logger.info("{}", handler.getResponse().getContentAsString().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(strategies.get(0))));
  }

}
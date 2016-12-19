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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;

import org.jimsey.projects.turbine.fuel.domain.MarketEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.TickerMetadata;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceRealtime;
import org.jimsey.projects.turbine.inlet.domain.TickerMetadataProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javaslang.control.Option;

/**
 * There appears to be no easy way to use a real bean in a @WebMvcTest.
 * This means that a 'real' DogKennel' instance cannot be given to the FinanceController.
 * It is not practical to mock the entire DogKennel,
 * therefore this test is ignored in favour of @SpringBootTest FinanceControllerIT
 * @throws Exception
 */
@Deprecated
@RunWith(SpringRunner.class)
@WebMvcTest(FinanceController.class)
public class FinanceControllerTest {

  private final MarketEnum market = MarketEnum.FTSE100;
  
  private final String symbol = "TEST";
      
  private final String name = "TESTName"; 

  private final Ticker ticker = Ticker.of(symbol, market);

  private final TickerMetadata metadata = new TickerMetadata(ticker, name);

  @MockBean
  private TickerMetadataProvider SymbolMetadataProvider;
  
  @Autowired
  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    // we need to mock beans when running a @WebMvcTest...
    given(SymbolMetadataProvider.findMetadataForMarketAndSymbol(market.toString(), symbol)).willReturn(Option.of(metadata));
    given(SymbolMetadataProvider.findMetadataForTicker(ticker)).willReturn(Option.of(metadata));
  }
  
  @Ignore
  @Test
  public void testYahooFinanceRealtime() throws Exception {
    MvcResult result = mvc
        .perform(get(format("/finance/yahoo/realtime/%s", symbol))
        .accept(MediaType.APPLICATION_OCTET_STREAM))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(not(isEmptyOrNullString())))
        .andReturn();
    
    MockHttpServletResponse response = result.getResponse();
    String body = response.getContentAsString();
    
    YahooFinanceRealtime yfr = YahooFinanceRealtime.of(metadata, OffsetDateTime.now(), body);

    assertThat(yfr).isNotNull();
    assertThat(yfr.getMetadata()).isEqualTo(metadata);
    assertThat(yfr.getOpen()).isGreaterThan(0);
    assertThat(yfr.getHigh()).isGreaterThan(0);
    assertThat(yfr.getLow()).isGreaterThan(0);
    assertThat(yfr.getClose()).isGreaterThan(0);
    assertThat(yfr.getVol()).isGreaterThan(0);
    
    // can't seem to have tuples with collection values in javaslang, sort of understandable...
    // Map<String, List<String>> map = List.ofAll(response.getHeaderNames()).toMap(n -> Tuple.ofAll(n, List.ofAll(response.getHeaders("n"))));
    
  }
}
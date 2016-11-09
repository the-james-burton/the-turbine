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

import org.jimsey.projects.turbine.inlet.domain.Market;
import org.jimsey.projects.turbine.inlet.domain.SymbolMetadata;
import org.jimsey.projects.turbine.inlet.domain.SymbolMetadataProvider;
import org.jimsey.projects.turbine.inlet.domain.YahooFinanceRealtime;
import org.junit.Before;
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

@RunWith(SpringRunner.class)
@WebMvcTest(FinanceController.class)
public class FinanceControllerTest {

  private final Market market = Market.FTSE100;
  
  private final String symbol = "testSymbol";
      
  private final String name = "testName"; 
      
  private final SymbolMetadata metadata = new SymbolMetadata(market, symbol, name);

  @MockBean
  private SymbolMetadataProvider SymbolMetadataProvider;
  
  @Autowired
  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    // we need to mock beans when running a @WebMvcTest...
    given(SymbolMetadataProvider.findMetadataForMarketAndSymbol(market.toString(), symbol)).willReturn(metadata);
  }
  
  @Test
  public void testYahooFinanceRealtime() throws Exception {
    MvcResult result = mvc
        .perform(get(format("/finance/yahoo/realtime/%s", symbol))
        .accept(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(not(isEmptyOrNullString())))
        .andReturn();
    
    MockHttpServletResponse response = result.getResponse();
    String body = response.getContentAsString();
    
    YahooFinanceRealtime yfr = new YahooFinanceRealtime(metadata, OffsetDateTime.now(), body);

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
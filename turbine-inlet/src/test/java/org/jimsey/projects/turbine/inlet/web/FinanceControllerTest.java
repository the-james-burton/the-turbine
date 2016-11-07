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

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.jimsey.projects.turbine.inlet.domain.Market;
import org.jimsey.projects.turbine.inlet.domain.SymbolMetadata;
import org.jimsey.projects.turbine.inlet.domain.SymbolMetadataProvider;
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

  @MockBean
  private SymbolMetadataProvider SymbolMetadataProvider;
  
  @Autowired
  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    // we need to mock beans when running a @WebMvcTest...
    SymbolMetadata metadata = new SymbolMetadata(Market.FTSE100, "testSymbol", "testName");
    given(SymbolMetadataProvider.findMetadataForMarketAndSymbol(any(String.class), any(String.class))).willReturn(metadata);
  }
  
  @Test
  public void testPing() throws Exception {
    MvcResult result = mvc
        .perform(get("/finance/yahoo/realtime/ABC")
        .accept(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andReturn();
    //.andExpect(status().isOk())
    //.andExpect(content().string(not(isEmptyOrNullString())));
    
    MockHttpServletResponse response = result.getResponse();
    String body = response.getContentAsString();
       
    // can't seem to have tuples with collection values in javaslang, sort of understandable...
    // Map<String, List<String>> map = List.ofAll(response.getHeaderNames()).toMap(n -> Tuple.ofAll(n, List.ofAll(response.getHeaders("n"))));
    
  }
}
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

import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.service.DomainObjectGenerator;
import org.jimsey.projects.turbine.spring.service.ElasticsearchService;
import org.jimsey.projects.turbine.spring.service.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.spring.web.TickController;
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
public class TickControllerTest {

  @InjectMocks
  private TickController controller;

  // @Spy
  @Mock
  private ElasticsearchService elasticsearch;

  private MockMvc mvc;

  private DomainObjectGenerator rdog = new RandomDomainObjectGenerator("FTSE100", "ABC");

  private List<TickJson> ticks = new ArrayList<TickJson>();

  private static ObjectMapper json = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    ticks = new ArrayList<TickJson>();
    ticks.add(rdog.newTick());
  }

  @Test
  public void testGetAllTicksGreaterThanDate() throws Exception {
    // use this for a spy...
    // Mockito.doReturn(123l).when(ping).ping();
    // String result =
    // "{\"date\":1437757461193,\"open\":93.31372449905724,\"high\":94.64656138818943,\"low\":92.35919077806433,\"close\":94.08436014274173,\"volume\":97.2503072332036,\"symbol\":\"ABC\",\"market\":\"FTSE100\",\"timestamp\":\"2015-07-24T18:04:21.193+01:00\"},
    // {\"date\":1437757457169,\"open\":95.76421881828955,\"high\":98.67332820497525,\"low\":92.87399277681914,\"close\":95.30416761402581,\"volume\":96.25382742497295,\"symbol\":\"ABC\",\"market\":\"FTSE100\",\"timestamp\":\"2015-07-24T18:04:17.169+01:00\"},
    // {\"date\":1437757455156,\"open\":95.20691875293008,\"high\":96.33109747791707,\"low\":95.03864535693057,\"close\":95.76421881828955,\"volume\":104.54628090784864,\"symbol\":\"ABC\",\"market\":\"FTSE100\",\"timestamp\":\"2015-07-24T18:04:15.156+01:00\"},
    // {\"date\":1437757459179,\"open\":95.30416761402581,\"high\":95.88765706158829,\"low\":92.37627010410178,\"close\":93.31372449905724,\"volume\":92.83201741698048,\"symbol\":\"ABC\",\"market\":\"FTSE100\",\"timestamp\":\"2015-07-24T18:04:19.179+01:00\"}";

    Mockito.when(elasticsearch.findBySymbolAndDateGreaterThan(Mockito.anyString(), Mockito.any(Long.class))).thenReturn(ticks);

    String expected = json.writeValueAsString(new Object() {
      @JsonProperty("ticks")
      List<TickJson> tickz = ticks;
    });

    long date = Instant.now().minus(1, ChronoUnit.MINUTES).toEpochMilli();

    String restUri = String.format("%s/all/%s", TurbineConstants.REST_ROOT_TICKS, date);

    mvc.perform(MockMvcRequestBuilders.get(restUri).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(expected)));
  }
}
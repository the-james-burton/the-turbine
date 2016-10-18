package org.jimsey.projects.turbine.inlet.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
public class TestControllerTest {

  // @Autowired
  // private TestRestTemplate restTemplate;

  @Autowired
  private MockMvc mvc;

  // @MockBean
  // private Ping ping;

  @Test
  public void testPing() throws Exception {
    // given(ping.ping()).willReturn(123l);

    // TODO how to do numeric comparison on string return..?
    mvc.perform(get("/ping").accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(content().string(not(isEmptyOrNullString())));
  }
}
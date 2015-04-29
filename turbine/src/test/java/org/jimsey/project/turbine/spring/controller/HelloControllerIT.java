package org.jimsey.project.turbine.spring.controller;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.jimsey.projects.turbine.spring.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=48001" })
@ActiveProfiles("consumer")
public class HelloControllerIT {

  @Value("${local.server.port}")
  private int port;

  private URL base;

  private RestTemplate template;

  @Before
  public void setUp() throws Exception {
    this.base = new URL("http://localhost:" + port + "/ping");
    template = new TestRestTemplate();
  }

  @Test
  public void getHello() throws Exception {
    ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
    assertNotNull(response.getBody());
  }
}
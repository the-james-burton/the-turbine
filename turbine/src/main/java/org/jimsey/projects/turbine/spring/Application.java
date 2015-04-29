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
package org.jimsey.projects.turbine.spring;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.jimsey.projects.turbine.spring.service.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application extends CamelConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  @NotNull
  Environment mEnvironment;

  public static void main(final String[] args) {
    ConfigurableApplicationContext spring = SpringApplication.run(
        Application.class, args);

    // logBeanNames(spring);

    Ping ping = (Ping) spring.getBean("ping");
    long message = ping.ping();
    logger.info(String.format("ping=%s", message));

    // ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.writeValue(resultFile, value);

  }

  private static void logBeanNames(final ConfigurableApplicationContext spring) {
    logger.info("Let's inspect the beans provided by Spring Boot:");
    String[] beanNames = spring.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      System.out.println(beanName);
    }
  }

}
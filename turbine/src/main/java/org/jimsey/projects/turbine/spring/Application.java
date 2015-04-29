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
  Environment environment;

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
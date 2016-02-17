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
package org.jimsey.projects.turbine.condenser;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.condenser.service.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Runtime entry point to the application. Contains the main method and runtime factories.
 *
 * @author the-james-burton
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableSwagger2
@EnableElasticsearchRepositories(basePackages = "org.jimsey.projects.turbine.condenser.elasticsearch.repositories")
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  @NotNull
  Environment environment;

  public static void main(final String... args) {
    ConfigurableApplicationContext spring = SpringApplication.run(Application.class, args);

    // logBeanNames(spring);
    Ping ping = (Ping) spring.getBean("ping");
    long message = ping.ping();
    logger.info(String.format("ping=%s", message));
  }

  @Bean
  public StockFactory stockFactory() {
    return new StockFactory() {
      public Stock createStock(String market, String symbol) {
        return runtimeStock(market, symbol);
      }
    };
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  Stock runtimeStock(String market, String symbol) {
    return new Stock(market, symbol);
  }

  @SuppressWarnings("unused")
  private static void logBeanNames(final ConfigurableApplicationContext spring) {
    logger.info("Let's inspect the beans provided by Spring Boot:");
    String[] beanNames = spring.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      System.out.println(beanName);
    }
  }

}
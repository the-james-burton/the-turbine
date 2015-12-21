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
package org.jimsey.projects.turbine.condenser.service;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.domain.indicators.EnableTurbineIndicator;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.condenser.elasticsearch.repositories.IndicatorRepository;
import org.jimsey.projects.turbine.fuel.domain.Stocks;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TurbineService {

  private static final Logger logger = LoggerFactory.getLogger(TurbineService.class);

  private static ObjectMapper json = new ObjectMapper();

  @Autowired
  @NotNull
  private IndicatorRepository indicatorRepository;

  @PostConstruct
  public void init() {
    logger.info("TurbineService: initialised");
    logger.info("found indicators : {}", listIndicators().toString());
    logger.info("found strategies : {}", listStrategies().toString());
  }

  public String listStocks(String market) {
    List<Stocks> stocks = Arrays.stream(Stocks.values())
        .filter(stock -> stock.getMarket().equals(market))
        .collect(Collectors.toList());

    // TODO is there a better way to push the list into a json property?
    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("stocks")
        List<Stocks> stockz = stocks;
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  public String listStrategies() {
    List<String> strategies = findAnnotationUsage(
        EnableTurbineStrategy.class.getPackage().getName(),
        EnableTurbineStrategy.class);

    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("strategies")
        List<String> strategiez = strategies;
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  public String listIndicators() {
    List<String> indicators = findAnnotationUsage(
        EnableTurbineIndicator.class.getPackage().getName(),
        EnableTurbineIndicator.class);

    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("indicators")
        List<String> indicatorz = indicators;
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  private List<String> findAnnotationUsage(String packageName, Class<? extends Annotation> annotation) {
    Reflections reflections = new Reflections(packageName);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
    return classes.stream()
        .map(clazz -> clazz.getSimpleName())
        .collect(Collectors.toList());
  }

}

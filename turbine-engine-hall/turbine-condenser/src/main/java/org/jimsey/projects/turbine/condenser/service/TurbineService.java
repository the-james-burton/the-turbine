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
package org.jimsey.projects.turbine.condenser.service;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorClientDefinition;
import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorInstance;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.fuel.domain.ExchangeEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

@Service
public class TurbineService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static ObjectMapper json = new ObjectMapper();

  public List<IndicatorInstance> indicatorInstances;

  @Autowired
  @NotNull
  private TickerManager tickerManager;

  @Autowired
  private ResourceLoader resource;

  // @Autowired
  // @NotNull
  // private IndicatorRepository indicatorRepository;

  @PostConstruct
  public void init() throws IOException {
    URL url = Resources.getResource("indicator.instances.txt");
    String instancesTxt = Resources.toString(url, StandardCharsets.UTF_8);
    TypeReference<List<IndicatorInstance>> listOfInstances = new TypeReference<List<IndicatorInstance>>() {
    };
    indicatorInstances = json.readValue(instancesTxt, listOfInstances);

    logger.info("TurbineService: initialised");
    // logger.info("found indicators : {}", listIndicators().toString());
    logger.info("loaded indicator instances: {}", indicatorInstances);
    logger.info("found strategies : {}", listStrategies().toString());
  }

  public List<Ticker> findTickers(ExchangeEnum exchange) {
    return tickerManager.getTickers()
        .filter(ticker -> ticker.getExchange().equals(exchange))
        .toJavaList();
  }

  public List<EnableTurbineStrategy> findStrategies() {
    Reflections reflections = new Reflections(EnableTurbineStrategy.class.getPackage().getName());
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(EnableTurbineStrategy.class);
    return classes.stream()
        .map(c -> c.getDeclaredAnnotation(EnableTurbineStrategy.class))
        .collect(Collectors.toList());
  }

  // public List<EnableTurbineIndicator> findIndicators() {
  // Reflections reflections = new Reflections(EnableTurbineIndicator.class.getPackage().getName());
  // Set<Class<?>> classes = reflections.getTypesAnnotatedWith(EnableTurbineIndicator.class);
  // return classes.stream()
  // .map(c -> c.getDeclaredAnnotation(EnableTurbineIndicator.class))
  // .collect(Collectors.toList());
  // }

  public String listStocks(String exchange) {

    // TODO is there a better way to push the list into a json property?
    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("stocks")
        List<Ticker> stockz = findTickers(ExchangeEnum.valueOf(exchange));
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  public String listStrategies() {
    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("strategies")
        List<EnableTurbineStrategy> strategiez = findStrategies();
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  public String listIndicators() {
    String result = null;
    try {
      result = json.writeValueAsString(new Object() {
        @JsonProperty("indicators")
        List<IndicatorClientDefinition> indicatorz = indicatorInstances.stream().map(i -> i.getClientIndicator()).collect(toList());
      });
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  public List<IndicatorInstance> getIndicatorInstances() {
    return indicatorInstances;
  }

  public ResourceLoader getResource() {
    return resource;
  }

  public void setResource(ResourceLoader resource) {
    this.resource = resource;
  }

}

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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.domain.indicators.EnableTurbineIndicator;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.condenser.elasticsearch.repositories.IndicatorRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TurbineService {

  private static final Logger logger = LoggerFactory.getLogger(TurbineService.class);

  @Autowired
  @NotNull
  private IndicatorRepository indicatorRepository;

  @PostConstruct
  public void init() {
    logger.info("TurbineService: initialised");
    logger.info("found indicators : {}", listIndicators().toString());
    logger.info("found strategies : {}", listStrategies().toString());
  }

  public List<String> listStrategies() {
    return findAnnotationUsage("org.jimsey.projects.turbine.condenser.domain.strategies", EnableTurbineStrategy.class);
  }

  public List<String> listIndicators() {
    return findAnnotationUsage("org.jimsey.projects.turbine.condenser.domain.indicators", EnableTurbineIndicator.class);
  }

  private List<String> findAnnotationUsage(String packageName, Class<? extends Annotation> annotation) {
    Reflections reflections = new Reflections(packageName);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
    return classes.stream()
        .map(clazz -> clazz.getName())
        .collect(Collectors.toList());
  }

}

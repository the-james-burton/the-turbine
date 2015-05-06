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
package org.jimsey.projects.turbine.spring.component;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.component.log.LogComponent;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.jimsey.projects.turbine.spring.camel.formatters.ToStringLogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelSetup {

  private static final Logger logger = LoggerFactory.getLogger(CamelSetup.class);

  @Autowired
  CamelContext mCamel;

  @PostConstruct
  public void init() {
    logger.info("setting up camel...");
    LogComponent slog = new LogComponent();
    slog.setExchangeFormatter(new ToStringLogFormatter());
    mCamel.setUseMDCLogging(true);
    mCamel.addComponent("slog", slog);
    mCamel.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
    logger.info("camel setup complete");
  }

}

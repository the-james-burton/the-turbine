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
package org.jimsey.projects.turbine.inlet.external.downloading;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * http://www.londonstockexchange.com/statistics/companies-and-issuers/list-of-all-companies.xls
 * http://www.londonstockexchange.com/statistics/companies-and-issuers/list-of-all-securities-ex-debt.xls
 *
 * @author the-james-burton
 */
@Component
@ManagedResource
// not using type safe properties here, just using @Value...
// @ConfigurationProperties(prefix = "infrastructure")
public class LseDownloader extends BaseDownloader {

  final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${exchanges.lse.companies.url}")
  private String lseCompaniesUrl;

  @Value("${exchanges.lse.securities.url}")
  private String lseSecuritiesUrl;

  @Value("${exchanges.lse.companies.file}")
  private String lseCompaniesFile;

  @Value("${exchanges.lse.securities.file}")
  private String lseSecuritiesFile;

  @PostConstruct
  public void init() {
    logger.info("exchanges.lse.companies.file: {}", lseCompaniesFile);
    logger.info("exchanges.lse.securities.file: {}", lseSecuritiesFile);
    logger.info("exchanges.lse.companies.url: {}", lseCompaniesUrl);
    logger.info("exchanges.lse.securities.url: {}", lseSecuritiesUrl);

    // TODO reduce the amount of downloading by keeping files for 1 month...
    // downloadAsync(lseCompaniesUrl, lseCompaniesFile);
    // downloadAsync(lseSecuritiesUrl, "securities.xls");
    // downloadAsync("http://google.com", "google.txt");
  }

  @Override
  Logger getLogger() {
    return logger;
  }

}
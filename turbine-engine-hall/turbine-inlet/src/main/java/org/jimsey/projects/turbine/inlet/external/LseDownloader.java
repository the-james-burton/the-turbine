package org.jimsey.projects.turbine.inlet.external;

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

  @Value("${markets.lse.companies.url}")
  private String lseCompaniesUrl;

  @Value("${markets.lse.securities.url}")
  private String lseSecuritiesUrl;

  @Value("${markets.lse.companies.file}")
  private String lseCompaniesFile;

  @Value("${markets.lse.securities.file}")
  private String lseSecuritiesFile;

  @PostConstruct
  public void init() {
    logger.info("markets.lse.companies.file: {}", lseCompaniesFile);
    logger.info("markets.lse.securities.file: {}", lseSecuritiesFile);
    logger.info("markets.lse.companies.url: {}", lseCompaniesUrl);
    logger.info("markets.lse.securities.url: {}", lseSecuritiesUrl);

    // TODO reduce the amount of downloading by keeping files for 1 month...
    // downloadAsync(lseCompaniesUrl, lseCompaniesFile);
    // downloadAsync(lseSecuritiesUrl, "securities.xls");
    downloadAsync("http://google.com", "google.txt");
  }

  @Override
  Logger getLogger() {
    return logger;
  }

}
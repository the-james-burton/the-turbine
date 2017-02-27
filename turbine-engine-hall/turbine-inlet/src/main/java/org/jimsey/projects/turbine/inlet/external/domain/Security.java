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
package org.jimsey.projects.turbine.inlet.external.domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Security implements Comparable<Security> {

  // Security Start Date, Company Name, Country of Incorporation, LSE Market, FCA Listing Category, ISIN, Security Name, TIDM, Mkt
  // Cap £m, Shares in Issue, Industry, Supersector, Sector, Subsector, Group, MarketSegmentCode, MarketSectorCode, Trading
  // Currency
  //
  // 02-Aug-06, 1PM PLC, GB, AIM, GB00BCDBXK43, ORD GBP0.1, OPM , 29.97210315, 54,494,733.00, Financials, Financial Services,
  // Financial Services, Specialty Finance, 8775, AIM, AIM, GBX

  // Security Start Date:NUMERIC, Company Name:STRING, Country of Incorporation:STRING, LSE Market:STRING, FCA Listing
  // Category:STRING, ISIN:STRING, Security Name:STRING, TIDM:STRING, Mkt Cap £m:NUMERIC, Shares in Issue:NUMERIC,
  // Industry:STRING, Supersector:STRING, Sector:STRING, Subsector:STRING, Group:NUMERIC, MarketSegmentCode:STRING,
  // MarketSectorCode:STRING, Trading Currency:STRING

  // LocalDate: row.getCell(0).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
  // String: StringUtils.trim(row.getCell(1).getStringCellValue()),
  // Integer: (int) row.getCell(2).getNumericCellValue(),
  // Double: row.getCell(7).getNumericCellValue(),
  // Boolean: convertToBoolean.apply(row.getCell(8).getStringCellValue())))

  public final LocalDate securityStartDate;

  public final String companyName;

  public final String countryOfIncorporation;

  public final String lseMarket;

  public final String fcaListingCategory;

  public final String isin;

  public final String securityName;

  public final String tidm;

  public final Double mktCap;

  public final Long sharesInIssue;

  public final String industry;

  public final String supersector;

  public final String sector;

  public final String subsector;

  public final Integer group;

  public final String marketSegmentCode;

  public final String marketSectorCode;

  public final String tradingCurrency;

  private final Comparator<Security> comparator = Comparator
      .comparing((Security s) -> s.securityName)
      .thenComparing((Security s) -> s.companyName)
      .thenComparing(s -> s.lseMarket)
      .thenComparing(s -> s.securityStartDate)
      .thenComparing(s -> s.isin);

  public Security(LocalDate securityStartDate, String companyName, String countryOfIncorporation, String lseMarket,
      String fcaListingCategory, String isin, String securityName, String tidm, Double mktCap, Long sharesInIssue, String industry,
      String supersector, String sector, String subsector, Integer group, String marketSegmentCode, String marketSectorCode,
      String tradingCurrency) {
    this.securityStartDate = securityStartDate;
    this.companyName = companyName;
    this.countryOfIncorporation = countryOfIncorporation;
    this.lseMarket = lseMarket;
    this.fcaListingCategory = fcaListingCategory;
    this.isin = isin;
    this.securityName = securityName;
    this.tidm = tidm;
    this.mktCap = mktCap;
    this.sharesInIssue = sharesInIssue;
    this.industry = industry;
    this.supersector = supersector;
    this.sector = sector;
    this.subsector = subsector;
    this.group = group;
    this.marketSegmentCode = marketSegmentCode;
    this.marketSectorCode = marketSectorCode;
    this.tradingCurrency = tradingCurrency;
  }

  public static Security of(LocalDate securityStartDate, String companyName, String countryOfIncorporation, String lseMarket,
      String fcaListingCategory, String isin, String securityName, String tidm, Double mktCap, Long sharesInIssue, String industry,
      String supersector, String sector, String subsector, Integer group, String marketSegmentCode, String marketSectorCode,
      String tradingCurrency) {
    return new Security(securityStartDate, companyName, countryOfIncorporation, lseMarket,
        fcaListingCategory, isin, securityName, tidm, mktCap, sharesInIssue, industry,
        supersector, sector, subsector, group, marketSegmentCode, marketSectorCode, tradingCurrency);
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof Company)) {
      return false;
    }
    Security that = (Security) key;
    return Objects.equals(this.securityName, that.securityName)
        && Objects.equals(this.companyName, that.companyName)
        && Objects.equals(this.lseMarket, that.lseMarket)
        && Objects.equals(this.securityStartDate, that.securityStartDate)
        && Objects.equals(this.isin, that.isin);
  }

  @Override
  public int compareTo(Security that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(securityName, companyName, lseMarket, securityStartDate, isin);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  // ---------------------------------
  public LocalDate getSecurityStartDate() {
    return securityStartDate;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getCountryOfIncorporation() {
    return countryOfIncorporation;
  }

  public String getLseMarket() {
    return lseMarket;
  }

  public String getFcaListingCategory() {
    return fcaListingCategory;
  }

  public String getIsin() {
    return isin;
  }

  public String getSecurityName() {
    return securityName;
  }

  public String getTidm() {
    return tidm;
  }

  public Double getMktCap() {
    return mktCap;
  }

  public Long getSharesInIssue() {
    return sharesInIssue;
  }

  public String getIndustry() {
    return industry;
  }

  public String getSupersector() {
    return supersector;
  }

  public String getSector() {
    return sector;
  }

  public String getSubsector() {
    return subsector;
  }

  public Integer getGroup() {
    return group;
  }

  public String getMarketSegmentCode() {
    return marketSegmentCode;
  }

  public String getMarketSectorCode() {
    return marketSectorCode;
  }

  public String getTradingCurrency() {
    return tradingCurrency;
  }

}

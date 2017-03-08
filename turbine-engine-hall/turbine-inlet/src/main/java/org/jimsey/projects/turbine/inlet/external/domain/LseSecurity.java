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

import static java.lang.String.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javaslang.control.Try;

/**
 * POJO representation of a row in the LSE securities spreadsheet
 * http://www.londonstockexchange.com/statistics/companies-and-issuers/list-of-all-securities-ex-debt.xls
 *
 * @author the-james-burton
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class LseSecurity implements Comparable<LseSecurity> {

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

  private final OffsetDateTime timestamp = OffsetDateTime.now();

  private static ObjectMapper json = new ObjectMapper();

  private final Comparator<LseSecurity> comparator = Comparator
      .comparing((LseSecurity s) -> s.securityName)
      .thenComparing((LseSecurity s) -> s.companyName)
      .thenComparing(s -> s.lseMarket)
      .thenComparing(s -> s.securityStartDate)
      .thenComparing(s -> s.isin);

  @JsonCreator
  public LseSecurity(
      @JsonProperty("securityStartDate") LocalDate securityStartDate,
      @JsonProperty("companyName") String companyName,
      @JsonProperty("countryOfIncorporation") String countryOfIncorporation,
      @JsonProperty("lseMarket") String lseMarket,
      @JsonProperty("fcaListingCategory") String fcaListingCategory,
      @JsonProperty("isin") String isin,
      @JsonProperty("securityName") String securityName,
      @JsonProperty("tidm") String tidm,
      @JsonProperty("mktCap") Double mktCap,
      @JsonProperty("sharesInIssue") Long sharesInIssue,
      @JsonProperty("industry") String industry,
      @JsonProperty("supersector") String supersector,
      @JsonProperty("sector") String sector,
      @JsonProperty("subsector") String subsector,
      @JsonProperty("group") Integer group,
      @JsonProperty("marketSegmentCode") String marketSegmentCode,
      @JsonProperty("marketSectorCode") String marketSectorCode,
      @JsonProperty("tradingCurrency") String tradingCurrency) {
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

  public static LseSecurity of(LocalDate securityStartDate, String companyName, String countryOfIncorporation, String lseMarket,
      String fcaListingCategory, String isin, String securityName, String tidm, Double mktCap, Long sharesInIssue, String industry,
      String supersector, String sector, String subsector, Integer group, String marketSegmentCode, String marketSectorCode,
      String tradingCurrency) {
    return new LseSecurity(securityStartDate, companyName, countryOfIncorporation, lseMarket,
        fcaListingCategory, isin, securityName, tidm, mktCap, sharesInIssue, industry,
        supersector, sector, subsector, group, marketSegmentCode, marketSectorCode, tradingCurrency);
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof LseCompany)) {
      return false;
    }
    LseSecurity that = (LseSecurity) key;
    return Objects.equals(this.securityName, that.securityName)
        && Objects.equals(this.companyName, that.companyName)
        && Objects.equals(this.lseMarket, that.lseMarket)
        && Objects.equals(this.securityStartDate, that.securityStartDate)
        && Objects.equals(this.isin, that.isin);
  }

  @Override
  public int compareTo(LseSecurity that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(securityName, companyName, lseMarket, securityStartDate, isin);
  }

  public String toStringForElasticsearch() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  @Override
  public String toString() {
    return Try.of(() -> json.writeValueAsString(this))
        .getOrElseThrow(e -> new RuntimeException(format("unable to write [%s] as String", this.toStringForElasticsearch())));
  }

  // ---------------------------------
  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
  }

  @JsonIgnore
  public OffsetDateTime getTimestampAsObject() {
    return timestamp;
  }

  @JsonProperty("securityStartDate")
  public String getSecurityStartDate() {
    return securityStartDate.format(DateTimeFormatter.ISO_DATE);
  }

  @JsonIgnore
  public LocalDate getSecurityStartDateAsObject() {
    return securityStartDate;
  }

  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  @JsonProperty("countryOfIncorporation")
  public String getCountryOfIncorporation() {
    return countryOfIncorporation;
  }

  @JsonProperty("lseMarket")
  public String getLseMarket() {
    return lseMarket;
  }

  @JsonProperty("fcaListingCategory")
  public String getFcaListingCategory() {
    return fcaListingCategory;
  }

  @JsonProperty("isin")
  public String getIsin() {
    return isin;
  }

  @JsonProperty("securityName")
  public String getSecurityName() {
    return securityName;
  }

  @JsonProperty("tidm")
  public String getTidm() {
    return tidm;
  }

  @JsonProperty("mktCap")
  public Double getMktCap() {
    return mktCap;
  }

  @JsonProperty("sharesInIssue")
  public Long getSharesInIssue() {
    return sharesInIssue;
  }

  @JsonProperty("industry")
  public String getIndustry() {
    return industry;
  }

  @JsonProperty("supersector")
  public String getSupersector() {
    return supersector;
  }

  @JsonProperty("sector")
  public String getSector() {
    return sector;
  }

  @JsonProperty("subsector")
  public String getSubsector() {
    return subsector;
  }

  @JsonProperty("group")
  public Integer getGroup() {
    return group;
  }

  @JsonProperty("marketSegmentCode")
  public String getMarketSegmentCode() {
    return marketSegmentCode;
  }

  @JsonProperty("marketSectorCode")
  public String getMarketSectorCode() {
    return marketSectorCode;
  }

  @JsonProperty("tradingCurrency")
  public String getTradingCurrency() {
    return tradingCurrency;
  }

}

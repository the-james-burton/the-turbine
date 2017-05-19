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

import io.vavr.control.Try;

/**
 * POJO representation of a row in the LSE companies spreadsheet
 * http://www.londonstockexchange.com/statistics/companies-and-issuers/list-of-all-companies.xls
 *
 * @author the-james-burton
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class LseCompany implements Comparable<LseCompany> {

  // List Date, Company, Group, Sector, Sub Sector, Country of Incorporation, Market, Mkt Cap £m, International Main Market
  // 02-Aug-06,1PM PLC, 8775, General Financial, Specialty Finance, GB, AIM, 29.97210315, N

  private final LocalDate listDate;

  private final String name;

  private final Integer group;

  private final String sector;

  private final String subSector;

  private final String country;

  private final String market;

  private final Double marketCapitalisation;

  private final Boolean internationalMainMarket;

  private final OffsetDateTime timestamp = OffsetDateTime.now();

  private static final ObjectMapper json = new ObjectMapper();

  private final Comparator<LseCompany> comparator = Comparator
      .comparing((LseCompany c) -> c.name)
      .thenComparing(c -> c.listDate)
      .thenComparing(c -> c.group);

  @JsonCreator
  public LseCompany(
      @JsonProperty("listDate") LocalDate listDate,
      @JsonProperty("name") String name,
      @JsonProperty("group") Integer group,
      @JsonProperty("sector") String sector,
      @JsonProperty("subSector") String subSector,
      @JsonProperty("country") String country,
      @JsonProperty("market") String market,
      @JsonProperty("marketCapitalisation") Double marketCapitalisation,
      @JsonProperty("internationalMainMarket") Boolean internationalMainMarket) {
    this.listDate = listDate;
    this.name = name;
    this.group = group;
    this.sector = sector;
    this.subSector = subSector;
    this.country = country;
    this.market = market;
    this.marketCapitalisation = marketCapitalisation;
    this.internationalMainMarket = internationalMainMarket;
  }

  public static LseCompany of(LocalDate listDate, String name, Integer group, String sector, String subSector, String country,
      String market, Double marketCapitalisation, Boolean internationalMainMarket) {
    return new LseCompany(listDate, name, group, sector, subSector, country, market, marketCapitalisation, internationalMainMarket);
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof LseCompany)) {
      return false;
    }
    LseCompany that = (LseCompany) key;
    return Objects.equals(this.name, that.name)
        && Objects.equals(this.listDate, that.listDate)
        && Objects.equals(this.group, that.group);
  }

  @Override
  public int compareTo(LseCompany that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, listDate, group);
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

  @JsonProperty("listDate")
  public String getDate() {
    // return listDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    return listDate.format(DateTimeFormatter.ISO_DATE);
  }

  @JsonIgnore
  public LocalDate getListDateAsObject() {
    return listDate;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("group")
  public Integer getGroup() {
    return group;
  }

  @JsonProperty("sector")
  public String getSector() {
    return sector;
  }

  @JsonProperty("subSector")
  public String getSubSector() {
    return subSector;
  }

  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  @JsonProperty("market")
  public String getMarket() {
    return market;
  }

  @JsonProperty("marketCapitalisation")
  public Double getMarketCapitalisation() {
    return marketCapitalisation;
  }

  @JsonProperty("internationalMainMarket")
  public Boolean getInternationalMainMarket() {
    return internationalMainMarket;
  }

}

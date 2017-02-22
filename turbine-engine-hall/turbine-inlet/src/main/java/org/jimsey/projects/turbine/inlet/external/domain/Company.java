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

/**
 * POJO representation of a row in the LSE companies spreadsheet
 * http://www.londonstockexchange.com/statistics/companies-and-issuers/list-of-all-companies.xls
 *
 * @author the-james-burton
 */
public class Company implements Comparable<Company> {

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

  private final Comparator<Company> comparator = Comparator
      .comparing((Company c) -> c.name)
      .thenComparing(c -> c.listDate)
      .thenComparing(c -> c.group);

  public Company(LocalDate listDate, String name, Integer group, String sector, String subSector, String country, String market,
      Double marketCapitalisation, Boolean internationalMainMarket) {
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

  public static Company of(LocalDate listDate, String name, Integer group, String sector, String subSector, String country,
      String market, Double marketCapitalisation, Boolean internationalMainMarket) {
    return new Company(listDate, name, group, sector, subSector, country, market, marketCapitalisation, internationalMainMarket);
  }

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof Company)) {
      return false;
    }
    Company that = (Company) key;
    return Objects.equals(this.name, that.name)
        && Objects.equals(this.listDate, that.listDate)
        && Objects.equals(this.group, that.group);
  }

  @Override
  public int compareTo(Company that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, listDate, group);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  // ---------------------------------
  public LocalDate getListDate() {
    return listDate;
  }

  public String getName() {
    return name;
  }

  public Integer getGroup() {
    return group;
  }

  public String getSector() {
    return sector;
  }

  public String getSubSector() {
    return subSector;
  }

  public String getCountry() {
    return country;
  }

  public String getMarket() {
    return market;
  }

  public Double getMarketCapitalisation() {
    return marketCapitalisation;
  }

  public Boolean getInternationalMainMarket() {
    return internationalMainMarket;
  }

}

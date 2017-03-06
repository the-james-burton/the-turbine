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
package org.jimsey.projects.turbine.inlet.external.parsing;

import static java.lang.String.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jimsey.projects.turbine.inlet.external.domain.Company;
import org.jimsey.projects.turbine.inlet.external.domain.Security;
import org.jimsey.projects.turbine.inlet.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javaslang.Function1;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Stream;
import javaslang.control.Try;

@Component
@ManagedResource
public class LseParser {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  @Autowired
  @NotNull
  private ElasticsearchService elasticsearch;

  @Value("${exchanges.lse.companies.file}")
  private String lseCompaniesFile;

  @Value("${exchanges.lse.securities.file}")
  private String lseSecuritiesFile;

  private final Integer headerRowNumber = 6;

  private final List<String> expectedCompanyHeaders = List.of("List Date", "Company", "Group", "Sector", "Sub Sector",
      "Country of Incorporation", "Market", "Mkt Cap £m", "International Main Market");

  private final List<String> expectedSecurityHeaders = List.of(
      "Security Start Date", "Company Name", "Country of Incorporation", "LSE Market", "FCA Listing Category", "ISIN",
      "Security Name", "TIDM", "Mkt Cap £m", "Shares in Issue", "Industry", "Supersector", "Sector", "Subsector", "Group",
      "MarketSegmentCode", "MarketSectorCode", "Trading Currency");

  // POI needs help parsing 'N' and 'Y' string cells into a boolean...
  private static Function1<String, Boolean> convertToBoolean = (v) -> "Y".equals(v) ? true : false;

  @PostConstruct
  public void init() {
    logger.info("exchanges.lse.companies.file: {}", lseCompaniesFile);
    logger.info("exchanges.lse.securities.file: {}", lseSecuritiesFile);

    List<Company> companies = parseCompanies(lseCompaniesFile);
    companies.forEach(c -> logger.info(c.toString()));

    elasticsearch.deleteCompaniesIndex();
    elasticsearch.indexCompany(companies.head());

    List<Security> securities = parseSecurities(lseSecuritiesFile);
    securities.forEach(c -> logger.info(c.toString()));

    elasticsearch.deleteSecuritiesIndex();
    elasticsearch.indexSecurity(securities.head());
  }

  private List<Security> parseSecurities(String input) {
    Sheet sheet = extractSheet(input);

    // validate the sheet...
    Row headerRow = findAndValidateHeaderRow(sheet, headerRowNumber, expectedSecurityHeaders);

    logger.info(findHeaderCellTypes(sheet, headerRow)
        .map(t -> format("%s:%s", t._1, t._2))
        .reduce((a, b) -> format("%s, %s", a, b)));

    // Security Start Date:NUMERIC, Company Name:STRING, Country of Incorporation:STRING, LSE Market:STRING, FCA Listing
    // Category:STRING, ISIN:STRING, Security Name:STRING, TIDM:STRING, Mkt Cap £m:NUMERIC, Shares in Issue:NUMERIC,
    // Industry:STRING, Supersector:STRING, Sector:STRING, Subsector:STRING, Group:NUMERIC, MarketSegmentCode:STRING,
    // MarketSectorCode:STRING, Trading Currency:STRING

    // parse the sheet into Company pojos...
    List<Security> securities = Stream.ofAll((Iterable<Row>) sheet)
        .filter(row -> row.getRowNum() > 6)
        .map(row -> Security.of(
            row.getCell(0).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            StringUtils.trim(row.getCell(1).getStringCellValue()),
            StringUtils.trim(row.getCell(2).getStringCellValue()),
            StringUtils.trim(row.getCell(3).getStringCellValue()),
            StringUtils.trim(row.getCell(4).getStringCellValue()),
            StringUtils.trim(row.getCell(5).getStringCellValue()),
            StringUtils.trim(row.getCell(6).getStringCellValue()),
            StringUtils.trim(row.getCell(7).getStringCellValue()),
            row.getCell(8).getNumericCellValue(),
            (long) row.getCell(9).getNumericCellValue(),
            StringUtils.trim(row.getCell(10).getStringCellValue()),
            StringUtils.trim(row.getCell(11).getStringCellValue()),
            StringUtils.trim(row.getCell(12).getStringCellValue()),
            StringUtils.trim(row.getCell(13).getStringCellValue()),
            (int) row.getCell(14).getNumericCellValue(),
            StringUtils.trim(row.getCell(15).getStringCellValue()),
            StringUtils.trim(row.getCell(16).getStringCellValue()),
            StringUtils.trim(row.getCell(17).getStringCellValue())))
        .toList();

    return securities;
  }

  private List<Company> parseCompanies(String input) {
    Sheet sheet = extractSheet(input);

    // validate the sheet...
    Row headerRow = findAndValidateHeaderRow(sheet, headerRowNumber, expectedCompanyHeaders);

    // log out the cell types...
    logger.info(findHeaderCellTypes(sheet, headerRow)
        .map(t -> format("%s:%s", t._1, t._2))
        .reduce((a, b) -> format("%s, %s", a, b)));

    // parse the sheet into Company pojos...
    List<Company> companies = Stream.ofAll((Iterable<Row>) sheet)
        .filter(row -> row.getRowNum() > 6)
        .map(row -> Company.of(
            row.getCell(0).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            StringUtils.trim(row.getCell(1).getStringCellValue()),
            (int) row.getCell(2).getNumericCellValue(),
            StringUtils.trim(row.getCell(3).getStringCellValue()),
            StringUtils.trim(row.getCell(4).getStringCellValue()),
            StringUtils.trim(row.getCell(5).getStringCellValue()),
            StringUtils.trim(row.getCell(6).getStringCellValue()),
            row.getCell(7).getNumericCellValue(),
            convertToBoolean.apply(row.getCell(8).getStringCellValue())))
        .toList();

    return companies;
  }

  private Sheet extractSheet(String input) {
    InputStream xls = Try.of(() -> new FileInputStream(input))
        .getOrElseThrow(t -> new RuntimeException(format("unable to open stream for %s, reason:%s", input, t.getMessage())));
    Workbook wb = Try.of(() -> WorkbookFactory.create(xls))
        .getOrElseThrow(t -> new RuntimeException(format("unable to open workbook %s, reason:%s", xls, t.getMessage())));
    Sheet sheet = wb.getSheetAt(0);
    return sheet;
  }

  private List<Tuple2<String, CellType>> findHeaderCellTypes(Sheet sheet, Row headersAtRow) {
    Row firstRow = sheet.getRow(headerRowNumber + 1);
    return Stream.ofAll(IteratorUtils.asIterable(headersAtRow.cellIterator()))
        .zip(IteratorUtils.asIterable(firstRow.cellIterator()))
        .map(t -> Tuple.of(t._1.getStringCellValue(), t._2.getCellTypeEnum()))
        .toList();
    // .toJavaMap(v -> Tuple.of(v._1.getStringCellValue(), v._2.getCellTypeEnum()));
    // .forEach(t -> logger.info("{}:{}", t._1.getStringCellValue(), t._2.getCellTypeEnum().toString()));
  }

  private Row findAndValidateHeaderRow(Sheet sheet, Integer headersAtRow, List<String> expected) {
    Row headerRow = sheet.getRow(headersAtRow);
    List<String> actualHeaders = Stream.ofAll(IteratorUtils.asIterable(headerRow.cellIterator()))
        .map(cell -> cell.getStringCellValue())
        .toList();
    boolean isValid = actualHeaders.containsAll(expected);
    if (!isValid) {
      throw new RuntimeException("The expected header row is not present in the expected place. Maybe the sheet has changed?");
    }
    return headerRow;
  }

}

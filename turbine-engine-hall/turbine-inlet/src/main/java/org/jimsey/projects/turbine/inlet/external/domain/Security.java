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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Security {

  private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  public final Date listDate;

  public final String name;

  public final String country;

  public final String region;

  public final String market;

  public final String fsaListingCategory;

  public final boolean ordShares;

  public final boolean depReceipts;

  public final boolean fixedInt;

  public final boolean warrants;

  public final String ISIN;

  public final String stockName;

  public final String EPIC;

  public Security(String line) throws ParseException {

    String[] fields = line.split("\t");

    if (!"".equals(fields[0])) {
      this.listDate = dateFormat.parse(fields[0]);
    } else {
      this.listDate = null;
    }
    this.name = fields[1].trim().replace("&", "&amp;").trim();
    this.country = fields[2].trim();
    this.region = fields[3].trim();
    this.market = fields[4].trim();
    this.fsaListingCategory = fields[5];
    this.ordShares = fields[6].equals("YES") ? true : false;
    this.depReceipts = fields[7].equals("YES") ? true : false;
    this.fixedInt = fields[8].equals("YES") ? true : false;
    this.warrants = fields[9].equals("YES") ? true : false;
    this.ISIN = fields[10].trim();
    this.stockName = fields[11].trim();
    this.EPIC = fields[12].replace(".", "").trim();
  }

}

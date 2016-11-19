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
package org.jimsey.projects.turbine.inlet.domain;

import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.inspector.junit.CompareToTheory;
import org.junit.experimental.theories.DataPoint;

public class TickerMetadataCompareToTest extends CompareToTheory {

  public static final Ticker ABC_L = Ticker.of("ABC.L");

  public static final Ticker ABC_AX = Ticker.of("ABC.AX");

  public static final Ticker DEF_L = Ticker.of("DEF.L");

  public static final Ticker GHI_AX = Ticker.of("GHI.AX");

  @DataPoint
  public static final TickerMetadata DP_ABC_L = TickerMetadata.of(ABC_L, "ABC_L_Name");

  @DataPoint
  public static final TickerMetadata DP_ABC_AX = TickerMetadata.of(ABC_AX, "ABC_AX_Name");

  @DataPoint
  public static final TickerMetadata DP_DEF_L = TickerMetadata.of(DEF_L, "DEF_L_Name");

  @DataPoint
  public static final TickerMetadata DP_GHI_AX = TickerMetadata.of(GHI_AX, "GHI_AX_Name");

}

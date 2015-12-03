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
package org.jimsey.projects.turbine.condenser.domain.strategies;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Rule;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.trading.rules.OverIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.UnderIndicatorRule;

/**
 * based on an example in Ta4j itself
 */
@EnableTurbineStrategy
public class CCICorrectionStrategy extends BaseStrategy {

  public CCICorrectionStrategy(TimeSeries series, ClosePriceIndicator closePriceIndicator) {
    super(series, "CCICorrectionStrategy", closePriceIndicator);
    if (series == null) {
      throw new IllegalArgumentException("Series cannot be null");
    }

    CCIIndicator longCci = new CCIIndicator(series, 200);
    CCIIndicator shortCci = new CCIIndicator(series, 5);
    Decimal plus100 = Decimal.HUNDRED;
    Decimal minus100 = Decimal.valueOf(-100);

    Rule entryRule = new OverIndicatorRule(longCci, plus100) // Bull trend
        .and(new UnderIndicatorRule(shortCci, minus100)); // Signal

    Rule exitRule = new UnderIndicatorRule(longCci, minus100) // Bear trend
        .and(new OverIndicatorRule(shortCci, plus100)); // Signal

    this.strategy = new Strategy(entryRule, exitRule);
  }

}

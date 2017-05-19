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
package org.jimsey.projects.turbine.inlet.domain;

import javax.annotation.PostConstruct;

import org.jimsey.projects.turbine.fuel.constants.TurbineFuelConstants;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.springframework.stereotype.Service;

import io.vavr.Function1;
import io.vavr.collection.CharSeq;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;

/**
 * 
 * @author the-james-burton
 */
@Service
public class TickerMetadata {

  private Set<Ticker> knownTickers = HashSet.empty();

  public Function1<CharSeq, Option<Ticker>> findTickerBySymbol = (symbol) -> knownTickers
      .find(ticker -> ticker.getRic().eq(symbol));

  @PostConstruct
  public void init() {
    // TODO issue #5 initialise this from external source...
    TurbineFuelConstants.PRESET_TICKERS
        .forEach(t -> addTicker(t));
  }

  public void addTicker(Ticker ticker) {
    knownTickers = knownTickers.add(ticker);
  }

  public void addTickers(List<Ticker> tickers) {
    knownTickers = knownTickers.addAll(tickers);
  }

}

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

import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.springframework.stereotype.Service;

import javaslang.Function1;
import javaslang.collection.CharSeq;
import javaslang.collection.List;
import javaslang.control.Option;

/**
 * 
 * @author the-james-burton
 */
@Service
public class TickerMetadata {

  private List<Ticker> knownTickers = List.empty();
  
  public Function1<CharSeq, Option<Ticker>> findTickerBySymbol = 
      (symbol) -> knownTickers.find(ticker -> ticker.getRic().eq(symbol));
  
  @PostConstruct
  public void init() {
    // TODO issue #5 initialise this from external source...
    knownTickers = knownTickers.append(Ticker.of("ABC.L", "ABCName"));
    knownTickers = knownTickers.append(Ticker.of("DEF.L", "DEFName"));
  }
  
}

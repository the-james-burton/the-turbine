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
package org.jimsey.projects.turbine.condenser.service;


import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.StockFactory;
import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javaslang.Function0;
import javaslang.collection.HashSet;
import javaslang.collection.Set;

@Service
public class TickerManager {

  @Autowired
  @NotNull
  private StockFactory stockFactory;

  private Set<Stock> stocks = HashSet.empty();
    
  Function0<Set<Ticker>> tickers = () -> stocks.map(stock -> stock.getTicker());
  
  Function0<Set<Ticker>> tickerCache = Function0.of(tickers).memoized();

  public void addTick(Ticker ticker) {
    if (!stocks.map(s -> s.getTicker()).contains(ticker)) {
      stocks = stocks.add(getStockFactory().createStock(ticker));
      tickerCache = Function0.of(tickers).memoized();
    };
  }
  
  public Stock findStock(Ticker ticker) {
    return stocks.filter(stock -> stock.getTicker().equals(ticker)).get();
  }
  
  // --------------------------------------
  public Set<Ticker> getTickers() {
    return tickerCache.apply();
  }

  public StockFactory getStockFactory() {
    return stockFactory;
  }

  public void setStockFactory(StockFactory stockFactory) {
    this.stockFactory = stockFactory;
  }

}

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


import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.condenser.domain.indicators.EnableTurbineIndicator;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javaslang.Function0;
import javaslang.Function2;
import javaslang.collection.HashSet;
import javaslang.collection.Set;

@Service
public class TickerManager {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

//  @Autowired
//  @NotNull
//  private StockFactory stockFactory;
  
  @Autowired
  @NotNull
  private TurbineService turbineService;

  private final List<EnableTurbineIndicator> turbineIndicators = new ArrayList<>();

  private final List<EnableTurbineStrategy> turbineStrategies = new ArrayList<>();

  private Set<Stock> stocks = HashSet.empty();
    
  private Function0<Set<Ticker>> tickers = () -> stocks.map(stock -> stock.getTicker());
  
  private Function0<Set<Ticker>> tickerCache = Function0.of(tickers).memoized();

  @PostConstruct
  public void init() {
    turbineIndicators.addAll(turbineService.getIndicators());
    turbineStrategies.addAll(turbineService.getStrategies());
  }

  Function2<Ticker, Function0<Stock>, Stock> getStockForTickerOrElse = (ticker, supplier) -> stocks.filter(stock -> stock.getTicker().equals(ticker)).getOrElse(supplier);
  
  // this is called multiple times because we are effectively multicasting inside rabbitMQ 
  // so let's cope with that in an interesting way...
  public Stock findOrCreateStock(Ticker ticker) {
    logger.info("findOrCreateStock:{}", ticker);
    return getStockForTickerOrElse.apply(ticker, () -> createStock(ticker));
  }
  
  public synchronized Stock createStock(Ticker ticker) {
    logger.info("createStock:{}", ticker);
    return getStockForTickerOrElse.apply(ticker, () -> addStock(ticker));
  }
  
  private Stock addStock(Ticker ticker) {
    logger.info("addStock:{}", ticker);
    Stock stock = Stock.of(ticker, turbineIndicators, turbineStrategies);
    stocks = stocks.add(stock);
    tickerCache = Function0.of(tickers).memoized();
    return stock;
  }
  
  // --------------------------------------
  public Set<Ticker> getTickers() {
    return tickerCache.apply();
  }

}

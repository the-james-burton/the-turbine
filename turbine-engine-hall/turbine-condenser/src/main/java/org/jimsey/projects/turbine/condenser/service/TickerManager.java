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
import org.jimsey.projects.turbine.condenser.domain.indicators.IndicatorInstance;
import org.jimsey.projects.turbine.condenser.domain.strategies.EnableTurbineStrategy;
import org.jimsey.projects.turbine.fuel.domain.ExchangeEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.vavr.Function0;
import io.vavr.Function2;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

@Service
public class TickerManager {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  @NotNull
  private Environment environment;

  @Autowired
  @NotNull
  private TurbineService turbineService;

  @NotNull
  @Autowired
  private ElasticsearchService elasticsearch;

  // private final List<EnableTurbineIndicator> turbineIndicators = new ArrayList<>();
  private final List<IndicatorInstance> turbineIndicators = new ArrayList<>();

  private final List<EnableTurbineStrategy> turbineStrategies = new ArrayList<>();

  private Set<Stock> stocks = HashSet.empty();

  private Set<Ticker> tickers = HashSet.empty();

  // old but still interesting memoised version below...
  // private Function0<Set<Ticker>> tickers = () -> stocks.map(stock -> stock.getTicker());
  // private Function0<Set<Ticker>> tickerCache = Function0.of(tickers).memoized();

  @PostConstruct
  public void init() {
    // turbineIndicators.addAll(turbineService.findIndicators());
    turbineIndicators.addAll(turbineService.getIndicatorInstances());
    turbineStrategies.addAll(turbineService.findStrategies());

    // environment.getActiveProfiles();

    // TODO temporary...!!!
    // if (true)
    // return;

    List<Ticker> tickersFromEs = elasticsearch.findTickersByExchange(ExchangeEnum.LSE);
    if (tickersFromEs == null || tickersFromEs.isEmpty()) {
      logger.warn(" !!!! WARNING ---- no tickers found in elasticsearch ---- !!!!");
    } else {
      tickers = HashSet.ofAll(tickersFromEs);
    }
    stocks = tickers.map(t -> Stock.of(t, turbineIndicators, turbineStrategies));
    tickers.forEach(t -> logger.info("{}", t.toString()));
  }

  Function2<Ticker, Function0<Stock>, Stock> findStockForTickerOrElse = (ticker, supplier) -> stocks
      .filter(stock -> stock.getTicker().equals(ticker)).getOrElse(supplier);

  /** We used to multicast inside rabbitMQ, resulting in ticks, indicators and strategies being out-of-order.
   * This meant that dynamic creation of new tickers here could come from several places all at once.
   * So we coped with that in an interesting way, as shown in the methods below.
   *
   * Since the move to reactor, I believe this is is now redundant, but I will leave it in place.
   * Also, when tickers are read from elasticsearch, we may want to reject unknown ticks anyway.
   *
   * @param ticker the Ticker from which to look up the Stock
   * @return the Stock for the Ticker, either from existing or new
   */
  public Stock findOrCreateStock(Ticker ticker) {
    return findStockForTickerOrElse.apply(ticker, () -> createStockIfFirst(ticker));
  }

  private synchronized Stock createStockIfFirst(Ticker ticker) {
    return findStockForTickerOrElse.apply(ticker, () -> createStock(ticker));
  }

  private Stock createStock(Ticker ticker) {
    logger.info("creating new Stock object for ticker:{}", ticker);
    tickers = tickers.add(ticker);
    Stock stock = Stock.of(ticker, turbineIndicators, turbineStrategies);
    stocks = stocks.add(stock);
    // tickerCache = Function0.of(tickers).memoized();
    return stock;
  }

  // --------------------------------------
  public Set<Ticker> getTickers() {
    // return tickerCache.apply();
    return tickers;
  }

}

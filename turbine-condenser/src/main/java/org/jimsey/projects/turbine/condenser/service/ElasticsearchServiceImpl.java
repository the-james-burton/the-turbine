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
package org.jimsey.projects.turbine.condenser.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.elasticsearch.repositories.IndicatorRepository;
import org.jimsey.projects.turbine.condenser.elasticsearch.repositories.TickRepository;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

  @Autowired
  @NotNull
  private ElasticsearchOperations elasticsearch;

  @Autowired
  @NotNull
  private TickRepository tickRepository;

  @Autowired
  @NotNull
  private IndicatorRepository indicatorRepository;

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  @PostConstruct
  public void init() {
    logger.info("ElasticsearchTemplate: {}", elasticsearch.getSetting(TickJson.class).toString());

    // Instrument instrument = rdog.newInstrument();
    // instrumentRepository.saveToIndex(instrument, "test-instrument");

    // instrumentRepository.save(instrument);
    // logger.info(Iterables.toString(instrumentRepository.findAll()));

    // Quote quote = rdog.newQuote();
    // quoteRepository.saveToIndex(quote, "test-quote");

    // logger.info("getAllTicks() : [{}]", getAllTicks());
    // logger.info("findBySymbol('DEF') : [{}]", tickRepository.findBySymbol("DEF"));
    // logger.info("findBySymbol('ABC') : [{}]", tickRepository.findBySymbol("ABC"));

  }

  /*
   * public String getAllQuotes() {
   * NativeSearchQuery query = new NativeSearchQueryBuilder()
   * .withIndices("test-*")
   * .withTypes("quote")
   * .build();
   * Iterable<Quote> quotes = quoteRepository.search(query);
   * return Joiner.on(',').join(quotes);
   * }
   */

  /*
   * (non-Javadoc)
   * @see org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#getAllTicks()
   */
  @Override
  public String getAllTicks() {
    logger.info("getAllTicks");
    NativeSearchQuery query = new NativeSearchQueryBuilder()
        .withIndices(infrastructureProperties.getElasticsearchIndexForTicks())
        .withTypes(infrastructureProperties.getElasticsearchTypeForTicks())
        .withPageable(new PageRequest(0, Integer.MAX_VALUE))
        .build();

    Iterable<TickJson> ticks = tickRepository.search(query);

    return Joiner.on(',').join(ticks);

  }

  /*
   * (non-Javadoc)
   * @see org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#findTicksBySymbol(java.lang.String)
   */
  @Override
  public List<TickJson> findTicksBySymbol(String symbol) {
    logger.info("findTicksBySymbol({})", symbol);
    return tickRepository.findBySymbol(symbol);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#findTicksBySymbolAndDateGreaterThan(java.lang.String,
   * java.lang.Long)
   */
  @Override
  public List<TickJson> findTicksBySymbolAndDateGreaterThan(String symbol, Long date) {
    logger.info("findTicksBySymbolAndDateGreaterThan({}, {})", symbol, date);
    return tickRepository.findBySymbolAndDateGreaterThan(symbol, date);
  }

  /*
   * (non-Javadoc)
   * @see org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#findIndicatorsBySymbol(java.lang.String)
   */
  @Override
  public List<IndicatorJson> findIndicatorsBySymbol(String symbol) {
    logger.info("findIndicatorsBySymbol({})", symbol);
    return indicatorRepository.findBySymbol(symbol);
  }

  /*
   * (non-Javadoc)
   * @see org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#findIndicatorsBySymbolAndDateGreaterThan(java.lang.
   * String, java.lang.Long)
   */
  @Override
  public List<IndicatorJson> findIndicatorsBySymbolAndDateGreaterThan(String symbol, Long date) {
    logger.info("findIndicatorsBySymbolAndDateGreaterThan({}, {})", symbol, date);
    return indicatorRepository.findBySymbolAndDateGreaterThan(symbol, date);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.jimsey.projects.turbine.condenser.service.ElasticsearchServiceI#findIndicatorsBySymbolAndNameAndDateGreaterThan(java.lang
   * .String, java.lang.String, java.lang.Long)
   */
  @Override
  public List<IndicatorJson> findIndicatorsBySymbolAndNameAndDateGreaterThan(String symbol, String name, Long date) {
    logger.info("findIndicatorsBySymbolAndDateGreaterThan({}, {}, {})", symbol, name, date);
    return indicatorRepository.findBySymbolAndNameAndDateGreaterThan(symbol, name, date);
  }

}

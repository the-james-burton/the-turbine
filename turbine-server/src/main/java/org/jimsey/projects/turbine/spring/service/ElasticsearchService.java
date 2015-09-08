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
package org.jimsey.projects.turbine.spring.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.domain.Quote;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.elasticsearch.repositories.InstrumentRepository;
import org.jimsey.projects.turbine.spring.elasticsearch.repositories.QuoteRepository;
import org.jimsey.projects.turbine.spring.elasticsearch.repositories.TickRepository;
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
public class ElasticsearchService {
  
  private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

  @Autowired
  @NotNull
  private ElasticsearchOperations elasticsearch;

  @Autowired
  @NotNull
  private InstrumentRepository instrumentRepository;

  @Autowired
  @NotNull
  private QuoteRepository quoteRepository;
  
  @Autowired
  @NotNull
  private TickRepository tickRepository;
  
  @PostConstruct
  public void init() {
    logger.info("ElasticsearchTemplate: {}", elasticsearch.getSetting(Instrument.class).toString());
    
    //Instrument instrument = rdog.newInstrument();
    //instrumentRepository.saveToIndex(instrument, "test-instrument");
    
    //instrumentRepository.save(instrument);
    //logger.info(Iterables.toString(instrumentRepository.findAll()));
    
    //Quote quote = rdog.newQuote();
    //quoteRepository.saveToIndex(quote, "test-quote");
    
    //logger.info("getAllTicks() : [{}]", getAllTicks());
    //logger.info("findBySymbol('DEF') : [{}]", tickRepository.findBySymbol("DEF"));
    //logger.info("findBySymbol('ABC') : [{}]", tickRepository.findBySymbol("ABC"));
        
  }

  public String getAllQuotes() {
    NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withIndices("test-*")
    .withTypes("quote")
    .build();
    
    Iterable<Quote> quotes = quoteRepository.search(query);
    
    return Joiner.on(',').join(quotes);
    
  }
  
  public String getAllTicks() {
    logger.info("getAllTicks");
    NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withIndices(TurbineConstants.ELASTICSEARCH_INDEX_FOR_TICKS)
    .withTypes(TurbineConstants.ELASTICSEARCH_TYPE_FOR_TICKS)
    .withPageable(new PageRequest(0,Integer.MAX_VALUE))
    .build();
    
    Iterable<TickJson> ticks = tickRepository.search(query);
    
    return Joiner.on(',').join(ticks);
    
  }

  public List<TickJson> findBySymbol(String symbol) {
    logger.info("findBySymbol({})", symbol);
    return tickRepository.findBySymbol(symbol);
  }

  public List<TickJson> findBySymbolAndDateGreaterThan(String symbol, Long date) {
    logger.info("findBySymbolAndDateGreaterThan({}, {})", symbol, date);
    return tickRepository.findBySymbolAndDateGreaterThan(symbol, date);
  }
  
}

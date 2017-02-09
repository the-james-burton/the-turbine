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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.service.pojo.TickPojo;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;

@Deprecated
// @Service
// @Profile("test1")
public class ElasticsearchServiceJestImpl implements ElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  private JestClient elasticsearch;

  @PostConstruct
  public void init() {
    // Construct a new Jest client according to configuration via factory
    JestClientFactory factory = new JestClientFactory();
    String elasticsearchUrl = String.format("http://%s:%s",
        infrastructureProperties.getElasticsearchHost(), infrastructureProperties.getElasticsearchRestPort());
    factory.setHttpClientConfig(new HttpClientConfig.Builder(elasticsearchUrl)
        .multiThreaded(true)
        .build());
    elasticsearch = factory.getObject();
    logger.info("JEST service init for: {}", elasticsearchUrl);

    // ********* REMOVE ***********
    // findTicksBySymbol("ABC");
  }

  @Override
  public String getAllTicks() {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    // searchSourceBuilder.query(QueryBuilders.matchQuery("type", "kimchy"));

    Search search = new Search.Builder(searchSourceBuilder.toString())
        .addIndex(infrastructureProperties.getElasticsearchIndexForTicks())
        .addType(infrastructureProperties.getElasticsearchTypeForTicks())
        .build();

    SearchResult result = null;
    try {
      result = elasticsearch.execute(search);
    } catch (IOException e) {
      logger.error("getAllTicks() could not execute query on elasticsearch: {}", search.toString());
    }
    return result.getJsonString();
  }

  @Override
  public List<TickJson> findTicksByTicker(String ticker) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchQuery("ticker", ticker));

    Search search = new Search.Builder(searchSourceBuilder.toString())
        .addIndex(infrastructureProperties.getElasticsearchIndexForTicks())
        .addType(infrastructureProperties.getElasticsearchTypeForTicks())
        .build();

    System.out.println(search.toString());

    SearchResult result = null;
    try {
      result = elasticsearch.execute(search);
    } catch (IOException e) {
      logger.error("getAllTicks() could not execute query on elasticsearch: {}, [{}]",
          e.getMessage(), search.toString());
    }

    // List<SearchResult.Hit<Article, Void>> hits = searchResult.getHits(Article.class);
    // @SuppressWarnings({ "unused", "deprecation" })
    // List<String> articles = result.getSourceAsObjectList(String.class);
    // @SuppressWarnings("unused")
    // Map json = result.getJsonMap();

    List<SearchResult.Hit<TickPojo, Void>> hits = result.getHits(TickPojo.class);

    for (Hit<TickPojo, Void> hit : hits) {
      TickPojo tick = hit.source;
      logger.info(tick.toString());
    }

    // List<SearchResult.Hit<TickJson, Void>> hits = result.getHits(TickJson.class);
    logger.info(result.toString());

    return null;

  }

  @Override
  public List<TickJson> findTicksByTickerAndDateGreaterThan(String ticker, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IndicatorJson> findIndicatorsByTicker(String ticker) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IndicatorJson> findIndicatorsByTickerAndDateGreaterThan(String ticker, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IndicatorJson> findIndicatorsByTickerAndNameAndDateGreaterThan(String ticker, String name, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String indexTick(TickJson tick) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String indexIndicator(IndicatorJson indicator) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String indexStrategy(StrategyJson strategy) {
    // TODO Auto-generated method stub
    return null;
  }

}

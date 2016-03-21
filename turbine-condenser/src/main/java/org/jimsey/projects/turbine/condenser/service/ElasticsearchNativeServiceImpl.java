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

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ElasticsearchNativeServiceImpl implements ElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  @NotNull
  private InfrastructureProperties infrastructureProperties;

  private TransportClient elasticsearch;

  private static ObjectMapper json = new ObjectMapper();

  private String indexForTicks;

  private String typeForTicks;

  @PostConstruct
  public void init() {
    // Node node = NodeBuilder.nodeBuilder().clusterName("elasticsearch").client(true).node();
    Settings settings = ImmutableSettings.settingsBuilder()
        .put("cluster.name", "elasticsearch").build();
    elasticsearch = new TransportClient(settings);
    String host = infrastructureProperties.getElasticsearchHost();
    Integer port = infrastructureProperties.getElasticsearchNativePort();
    indexForTicks = infrastructureProperties.getElasticsearchIndexForTicks();
    typeForTicks = infrastructureProperties.getElasticsearchTypeForTicks();
    logger.info(" *** connecting to : {}:{}", host, port);
    elasticsearch.addTransportAddress(new InetSocketTransportAddress(host, port));
  }

  @Override
  public String getAllTicks() {
    QueryBuilder queryBuilder = matchAllQuery();
    SearchResponse response = elasticsearch.prepareSearch(indexForTicks)
        .setQuery(queryBuilder)
        .execute().actionGet();
    for (SearchHit hit : response.getHits().getHits()) {
      System.out.println(hit.getSourceAsString());
    }
    String results = Arrays.stream(response.getHits().getHits())
        .map(hit -> hit.getSourceAsString())
        .reduce((a, b) -> String.format("%s,%s", a, b)).orElse("");
    return String.format("[%s]", results);
  }

  @Override
  public List<TickJson> findTicksBySymbol(String symbol) {
    QueryBuilder query = boolQuery()
        .must(matchQuery("symbol", symbol));
    System.out.println(query.toString());
    SearchResponse response = createTickQuery(query).execute().actionGet();
    List<TickJson> results = extractResults(response, TickJson.class);
    return results;
  }

  @Override
  public List<TickJson> findTicksBySymbolAndDateGreaterThan(String symbol, Long date) {
    QueryBuilder query = boolQuery()
        .must(matchQuery("symbol", symbol))
        .must(rangeQuery("date").from(date));
    System.out.println(query.toString());
    SearchResponse response = createTickQuery(query).execute().actionGet();
    List<TickJson> results = extractResults(response, TickJson.class);
    return results;
  }

  @Override
  public List<IndicatorJson> findIndicatorsBySymbol(String symbol) {
    QueryBuilder query = boolQuery().must(matchQuery("symbol", symbol));
    System.out.println(query.toString());
    SearchResponse response = createIndicatorQuery(query).execute().actionGet();
    List<IndicatorJson> results = extractResults(response, IndicatorJson.class);
    return results;
  }

  @Override
  public List<IndicatorJson> findIndicatorsBySymbolAndDateGreaterThan(String symbol, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IndicatorJson> findIndicatorsBySymbolAndNameAndDateGreaterThan(String symbol, String name, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  private SearchRequestBuilder createTickQuery(QueryBuilder query) {
    return createQuery(query,
        indexForTicks,
        typeForTicks);
  }

  private SearchRequestBuilder createIndicatorQuery(QueryBuilder query) {
    return createQuery(query,
        indexForTicks,
        typeForTicks);
  }

  private SearchRequestBuilder createQuery(QueryBuilder query, String type, String index) {
    return elasticsearch.prepareSearch()
        .setQuery(query).setTypes();
  }

  private <T> List<T> extractResults(SearchResponse response, Class<T> t) {
    List<T> results = new ArrayList<>();
    try {
      for (SearchHit hit : response.getHits().getHits()) {
        System.out.println(hit.getSourceAsString());
        results.add(json.readValue(hit.getSourceAsString(), t));
      }
    } catch (Exception e) {
      logger.error("error parsing to TickJson: [{}]", e.getMessage());
    }
    return results;
  }

  public InfrastructureProperties getInfrastructureProperties() {
    return infrastructureProperties;
  }

  public void setInfrastructureProperties(InfrastructureProperties infrastructureProperties) {
    this.infrastructureProperties = infrastructureProperties;
  }

}

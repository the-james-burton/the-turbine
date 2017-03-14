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
package org.jimsey.projects.turbine.fuel.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jimsey.projects.turbine.fuel.component.BaseInfrastructureProperties;
import org.jimsey.projects.turbine.fuel.domain.ExchangeEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseElasticsearchNativeServiceImpl implements BaseElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final int size = 5000;

  @Resource
  private BaseInfrastructureProperties properties;

  private TransportClient elasticsearch;

  private static ObjectMapper json = new ObjectMapper();

  @PostConstruct
  public void init() {
    Settings settings = Settings.builder()
        .put("cluster.name", properties.getElasticsearchCluster())
        .put("transport.type", "netty4")
        .build();
    elasticsearch = new PreBuiltTransportClient(settings);
    logger.info(" *** connecting to : {}:{}:{}",
        properties.getElasticsearchCluster(), properties.getElasticsearchHost(), properties.getElasticsearchNativePort());

    InetSocketAddress address = new InetSocketAddress(properties.getElasticsearchHost(), properties.getElasticsearchNativePort());
    InetSocketTransportAddress transport = new InetSocketTransportAddress(address);
    elasticsearch.addTransportAddress(transport);
  }

  @Override
  public List<Ticker> findTickersByExchange(ExchangeEnum exchange) {
    return queryElasticsearch(
        properties.getElasticsearchIndexForTickers(), properties.getElasticsearchTypeForTickers(),
        Ticker.class, matchQuery("exchange", exchange.toString()));
  }

  private <T> List<T> queryElasticsearch(String index, String type, Class<T> t, QueryBuilder... queries) {
    BoolQueryBuilder query = boolQuery();
    for (QueryBuilder q : queries) {
      query.must(q);
    }
    logger.debug("queryElasticsearch({}, {}, {})...\n{}",
        index, type, t.getSimpleName(), query.toString());
    List<T> results = null;
    try {
      SearchResponse response = elasticsearch.prepareSearch()
          .setIndices(index)
          .setTypes(type)
          .setQuery(query)
          .setSize(size)
          .get();
      results = extractResults(response, t);
    } catch (ElasticsearchException e) {
      logger.error("unable to query elasticsearch: {}:{}", e.getMessage(), query.toString());
      e.printStackTrace();
    }
    return results;
  }

  private <T> List<T> extractResults(SearchResponse response, Class<T> t) {
    List<T> results = new ArrayList<>();
    try {
      for (SearchHit hit : response.getHits().getHits()) {
        results.add(json.readValue(hit.getSourceAsString(), t));
      }
      // this map reduce is just to get the results on their own lines in the file...
      logger.debug("extractResults({})...\n{}", t.getSimpleName(),
          results.stream()
              .map(i -> i.toString())
              .reduce((a, b) -> String.format("%s\n%s", a, b)).orElse(""));
    } catch (Exception e) {
      logger.error("error parsing to TickJson: [{}]", e.getMessage());
    }
    return results;
  }

}
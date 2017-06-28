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
package org.jimsey.projects.turbine.furnace.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.service.BaseElasticsearchNativeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.vavr.control.Option;

@Service
public class ElasticsearchNativeServiceImpl extends BaseElasticsearchNativeServiceImpl implements ElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public Option<TickJson> findMostRecentTick(String ric) {
    QueryBuilder queryBuilder = matchQuery("ric", ric);
    List<TickJson> results = null;
    try {
      SearchResponse response = elasticsearch
          .prepareSearch(properties.getElasticsearchIndexForTicks())
          .setQuery(queryBuilder)
          .setTypes(properties.getElasticsearchTypeForTicks())
          .setSize(1)
          .addSort("date", SortOrder.DESC)
          .get();
      results = extractResults(response, TickJson.class);
    } catch (ElasticsearchException e) {
      logger.error("unable to query elasticsearch 'findMostRecentTick': {}:{}", e.getMessage(), ric);
      e.printStackTrace();
    }
    return results == null || results.isEmpty() ? Option.none() : Option.of(results.get(0));

  }

}

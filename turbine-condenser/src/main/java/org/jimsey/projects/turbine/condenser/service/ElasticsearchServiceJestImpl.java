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
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;

@Service
@Profile("test1")
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
  public List<TickJson> findTicksBySymbol(String symbol) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchQuery("symbol", symbol));

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
    /*
     * {"took":3,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":60,"max_score":1.8109303,"hits"
     * :[{"_index":"turbine-ticks","_type":"turbine-tick","_id":"AVNks82hyd3xrRcNh1Hl","_score":1.8109303,"_source":{"date":
     * 1457683418491,"open":102.78671114152444,"high":104.93154986139871,"low":100.46281730277755,"close":101.08888319502152,
     * "volume":105.86839958250964,"symbol":"ABC","market":"FTSE100","timestamp":"2016-03-11T08:03:38.491Z"}},{"_index":
     * "turbine-ticks","_type":"turbine-tick","_id":"AVNks64jyd3xrRcNh1Gz","_score":1.8109303,"_source":{"date":1457683410457,
     * "open":103.18504177497373,"high":104.33807368427489,"low":103.0643715647594,"close":103.17208718888355,"volume":105.
     * 51305870268634,"symbol":"ABC","market":"FTSE100","timestamp":"2016-03-11T08:03:30.457Z"}},{"_index":"turbine-ticks","_type"
     * :"turbine-tick","_id":"AVNks73fyd3xrRcNh1HL","_score":1.8109303,"_source":{"date":1457683414472,"open":103.16607845089332,
     * "high":103.23587472278165,"low":101.86633513770805,"close":103.07720918009662,"volume":101.4520502619952,"symbol":"ABC",
     * "market":"FTSE100","timestamp":"2016-03-11T08:03:34.472Z"}},{"_index":"turbine-ticks","_type":"turbine-tick","_id":
     * "AVNks29xyd3xrRcNh1FT","_score":1.8109303,"_source":{"date":1457683394381,"open":99.8783307158042,"high":100.98933475671058
     * ,"low":98.1008254966076,"close":100.15862807153496,"volume":94.39883861697953,"symbol":"ABC","market":"FTSE100","timestamp"
     * :"2016-03-11T08:03:14.382Z"}},{"_index":"turbine-ticks","_type":"turbine-tick","_id":"AVNks3dNyd3xrRcNh1Fh","_score":1.
     * 8109303,"_source":{"date":1457683396393,"open":100.15862807153496,"high":103.107871734528,"low":97.30008407969648,"close":
     * 100.31966002516393,"volume":103.85421218625038,"symbol":"ABC","market":"FTSE100","timestamp":"2016-03-11T08:03:16.393Z"}},{
     * "_index":"turbine-ticks","_type":"turbine-tick","_id":"AVNks2iiyd3xrRcNh1FH","_score":1.8109303,"_source":{"date":
     * 1457683392241,"open":100.0,"high":102.7878758803247,"low":98.87159567147856,"close":99.8783307158042,"volume":93.
     * 27681746458906,"symbol":"ABC","market":"FTSE100","timestamp":"2016-03-11T08:03:12.246Z"}},{"_index":"turbine-ticks","_type"
     * :"turbine-tick","_id":"AVNktCueyd3xrRcNh1Jz","_score":1.8109303,"_source":{"date":1457683442574,"open":102.52836642412814,
     * "high":103.03354243902268,"low":100.49632391463594,"close":102.25565539690206,"volume":103.87220227456706,"symbol":"ABC",
     * "market":"FTSE100","timestamp":"2016-03-11T08:04:02.574Z"}},{"_index":"turbine-ticks","_type":"turbine-tick","_id":
     * "AVNktEMnyd3xrRcNh1KY","_score":1.8109303,"_source":{"date":1457683448605,"open":103.14039632765534,"high":104.
     * 27326908595577,"low":102.24805102898934,"close":103.95917852428497,"volume":95.78024246972137,"symbol":"ABC","market":
     * "FTSE100","timestamp":"2016-03-11T08:04:08.605Z"}},{"_index":"turbine-ticks","_type":"turbine-tick","_id":
     * "AVNks-zbyd3xrRcNh1IT","_score":1.8109303,"_source":{"date":1457683426512,"open":102.27826986128854,"high":102.
     * 44653688683994,"low":101.10145415038654,"close":101.53673459528694,"volume":102.06290292807897,"symbol":"ABC","market":
     * "FTSE100","timestamp":"2016-03-11T08:03:46.512Z"}},{"_index":"turbine-ticks","_type":"turbine-tick","_id":
     * "AVNktMTQyd3xrRcNh1NX","_score":1.8109303,"_source":{"date":1457683481797,"open":105.94345285134114,"high":106.
     * 71152601744467,"low":104.28151634113266,"close":104.73072627326712,"volume":106.06014237645729,"symbol":"ABC","market":
     * "FTSE100","timestamp":"2016-03-11T08:04:41.798Z"}}]}}
     */

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
  public List<TickJson> findTicksBySymbolAndDateGreaterThan(String symbol, Long date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IndicatorJson> findIndicatorsBySymbol(String symbol) {
    // TODO Auto-generated method stub
    return null;
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

}

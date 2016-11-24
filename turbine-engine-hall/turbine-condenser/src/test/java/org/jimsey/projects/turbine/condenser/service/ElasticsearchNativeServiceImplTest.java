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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.util.List;

import org.apache.qpid.util.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
// @SpringApplicationConfiguration(classes = MockServletContext.class)
@ActiveProfiles("it")
// @Ignore
public class ElasticsearchNativeServiceImplTest extends SpringBootContextLoader {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String elasticsearchTmpDir = "./target/elasticsearch";

  private static final String tickerOne = "ABC.L";

  private static final String tickerTwo = "DEF.L";

  private static final String tickerThree = "GHI.L";

  @InjectMocks
  private ElasticsearchNativeServiceImpl service = new ElasticsearchNativeServiceImpl();

  @Mock
  private InfrastructureProperties infrastructureProperties;

  private final DomainObjectGenerator rdogOne = new RandomDomainObjectGenerator(tickerOne);

  private final DomainObjectGenerator rdogTwo = new RandomDomainObjectGenerator(tickerTwo);

  private final ObjectMapper json = new ObjectMapper();

  private static Client elasticsearch;

  private static Node node;

  private static final Integer elasticsearchNativePort = 9305;

  private static final Integer elasticsearchRestPort = 9205;

  private static final String elasticsearchCluster = "elasticsearch-test";

  private static final String elasticsearchHost = "localhost";

  private static final String indexForTicks = "turbine-ticks-test";

  private static final String typeForTicks = "turbine-tick-test";

  private static final String indexForIndicators = "turbine-indicators-test";

  private static final String typeForIndicators = "turbine-indicators-test";

  private static final String indexForStrategies = "turbine-strategies-test";

  private static final String typeForStrategies = "turbine-strategies-test";

  private boolean initialised = false;

  public ElasticsearchNativeServiceImplTest() {
  }

  @Before
  public void before() throws ElasticsearchException, JsonProcessingException, InterruptedException {

    if (!initialised) {
      MockitoAnnotations.initMocks(this);

      // TODO these are also present in the application-it.yml files, how to consolidate?
      when(infrastructureProperties.getElasticsearchCluster()).thenReturn(elasticsearchCluster);
      when(infrastructureProperties.getElasticsearchHost()).thenReturn(elasticsearchHost);
      when(infrastructureProperties.getElasticsearchNativePort()).thenReturn(elasticsearchNativePort);
      when(infrastructureProperties.getElasticsearchRestPort()).thenReturn(elasticsearchRestPort);
      when(infrastructureProperties.getElasticsearchIndexForTicks()).thenReturn(indexForTicks);
      when(infrastructureProperties.getElasticsearchTypeForTicks()).thenReturn(typeForTicks);
      when(infrastructureProperties.getElasticsearchIndexForIndicators()).thenReturn(indexForIndicators);
      when(infrastructureProperties.getElasticsearchTypeForIndicators()).thenReturn(typeForIndicators);
      when(infrastructureProperties.getElasticsearchIndexForStrategies()).thenReturn(indexForStrategies);
      when(infrastructureProperties.getElasticsearchTypeForStrategies()).thenReturn(typeForStrategies);

      // TODO should I expect this @PostConstruct be called automatically for me?
      service.init();
      initialised = true;
    }

  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    logger.info("setup()");
    FileUtils.delete(new File(elasticsearchTmpDir), true);

    Settings settings = Settings.builder()
        .put("path.home", elasticsearchTmpDir)
        .put("path.conf", elasticsearchTmpDir)
        .put("path.data", elasticsearchTmpDir)
        .put("path.work", elasticsearchTmpDir)
        .put("path.logs", elasticsearchTmpDir)
        .put("http.port", elasticsearchRestPort)
        .put("transport.tcp.port", elasticsearchNativePort)
        .put("index.number_of_shards", "1")
        .put("index.number_of_replicas", "0")
        .put("discovery.zen.ping.multicast.enabled", "false")
        .build();

    node = NodeBuilder.nodeBuilder()
        .data(true)
        .client(false)
        .settings(settings)
        .clusterName(elasticsearchCluster)
        .node();
    node.start();
    elasticsearch = node.client();
  }

  @After
  public void tearDown() {
    logger.debug("tearDown()");
    // delete whole index, not each id...
    deleteElasticsearch(indexForTicks);
    deleteElasticsearch(indexForIndicators);
    deleteElasticsearch(indexForStrategies);
    refreshElasticsearch();
  }

  @Test
  public void testGetAllTicks() throws Exception {
    int number = 12;
    logger.info("given any {} ticks", number);
    populateElasticsearch(rdogOne, number, indexForTicks, typeForTicks, null, TickJson.class);
    logger.info("it should return all ticks");
    String result = service.getAllTicks();
    logger.info(" *** getAllTicks(): {}", result);
    @SuppressWarnings("unchecked")
    List<TickJson> ticks = (List<TickJson>) json.readValue(result, List.class);
    logger.info("expected:{}, actual:{}", number, ticks.size());
    assertThat(ticks).hasSize(number);
    assertThat(result).contains("timestamp");
  }

  @Test
  public void testFindTicksByTicker() throws Exception {
    int numberOne = 5;
    int numberTwo = 7;
    logger.info("given {} {} ticks and {} {} ticks",
        numberOne, tickerOne, numberTwo, tickerTwo);
    populateElasticsearch(rdogOne, numberOne, indexForTicks, typeForTicks, null, TickJson.class);
    populateElasticsearch(rdogTwo, numberTwo, indexForTicks, typeForTicks, null, TickJson.class);
    logger.info("it should return {} {} ticks, {} {} ticks and 0 {} ticks",
        numberOne, tickerOne, numberTwo, tickerTwo, tickerThree);
    List<TickJson> resultOne = service.findTicksByTicker(tickerOne);
    List<TickJson> resultTwo = service.findTicksByTicker(tickerTwo);
    List<TickJson> resultThree = service.findTicksByTicker(tickerThree);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        tickerOne, resultOne.size(), numberOne);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        tickerTwo, resultTwo.size(), numberTwo);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        tickerThree, resultThree.size(), 0);
    assertThat(resultOne).hasSize(numberOne);
    assertThat(resultTwo).hasSize(numberTwo);
    assertThat(resultThree).hasSize(0);
  }

  @Test
  public void testFindIndicatorsByTicker() throws Exception {
    int numberOne = 5;
    int numberTwo = 7;
    logger.info("given {} {} indicators and {} {} indicators",
        numberOne, tickerOne, numberTwo, tickerTwo);
    String name = "indicator-name";
    populateElasticsearch(rdogOne, numberOne, indexForIndicators, typeForIndicators, name, IndicatorJson.class);
    populateElasticsearch(rdogTwo, numberTwo, indexForIndicators, typeForIndicators, name, IndicatorJson.class);
    logger.info("it should return {} {} indictors, {} {} indicators and 0 {} indicators",
        numberOne, tickerOne, numberTwo, tickerTwo, tickerThree);
    List<IndicatorJson> resultOne = service.findIndicatorsByTicker(tickerOne);
    List<IndicatorJson> resultTwo = service.findIndicatorsByTicker(tickerTwo);
    List<IndicatorJson> resultThree = service.findIndicatorsByTicker(tickerThree);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        tickerOne, resultOne.size(), numberOne);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        tickerTwo, resultTwo.size(), numberTwo);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        tickerThree, resultThree.size(), 0);
    assertThat(resultOne).hasSize(numberOne);
    assertThat(resultTwo).hasSize(numberTwo);
    assertThat(resultThree).hasSize(0);
  }

  @Test
  public void testFindTicksByTickerAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 4;
    logger.info("given {} {} ticks", number, tickerOne);
    long midpoint = populateElasticsearch(rdogOne, number, indexForTicks, typeForTicks, null, TickJson.class).getDate();
    logger.info("it should return only {} {} ticks after the midpoint", expected, tickerOne);
    List<TickJson> result = service.findTicksByTickerAndDateGreaterThan(tickerOne, midpoint);
    logger.info(" *** findTicksByTickerAndDateGreaterThan({}, {}): {}, expected: {}",
        tickerOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  @Test
  public void testFindIndicatorsByTickerAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 4;
    logger.info("given {} {} indicators", number, tickerOne);
    long midpoint = populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, null,
        IndicatorJson.class).getDate();
    logger.info("it should return only {} {} indicators after the midpoint", expected, tickerOne);
    List<IndicatorJson> result = service.findIndicatorsByTickerAndDateGreaterThan(tickerOne, midpoint);
    logger.info(" *** findIndicatorsByTickerAndDateGreaterThan({}, {}): {}, expected: {}",
        tickerOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  @Test
  public void testFindIndicatorsByTickerAndNameAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 7;
    final String nameOne = "indicator-one";
    final String nameTwo = "indicator-two";
    logger.info("given {} {} {} indicators and {} {} {} indicators",
        number, tickerOne, nameOne, number, tickerOne, nameTwo);
    populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, nameOne, IndicatorJson.class);
    long midpoint = OffsetDateTime.now().toInstant().toEpochMilli();
    populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, nameTwo, IndicatorJson.class);
    logger.info("it should return only {} {} {} indicators after the midpoint", expected, nameOne, tickerOne);
    List<IndicatorJson> result = service.findIndicatorsByTickerAndNameAndDateGreaterThan(tickerOne, nameOne, midpoint);
    logger.info(" *** findIndicatorsByTickerAndNameAndDateGreaterThan({}, {}, {}): {}, expected: {}",
        tickerOne, nameOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  // ------------------------------------------------------

  /**
   * Populate elasticsearch with the given number of the given objects
   * 
   * @param rdog the RandomDomainObjectGenerator to use
   * @param number how many objects to create
   * @param index the index to use in elasticsearch
   * @param type the type to use in elasticsearch
   * @param name the name of the indicator or strategy (ignored for ticks)
   * @param t what type of objects to create
   * @return the object in the middle of the populate, by timestamp
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private <T> T populateElasticsearch(
      DomainObjectGenerator rdog, int number, String index, String type, String name, Class<T> t)
      throws Exception {
    int x = 0;
    int midpoint = (number / 2) + 1;
    Object result = null;
    Object current = null;
    while (++x <= number) {
      // cannot use switch statement easily here due to generics...
      if (TickJson.class.getName().equals(t.getName())) {
        current = rdog.newTick();
      }
      if (IndicatorJson.class.getName().equals(t.getName())) {
        current = rdog.newIndicator(name);
      }
      if (StrategyJson.class.getName().equals(t.getName())) {
        current = rdog.newStrategy(name);
      }
      if (current == null) {
        throw new Exception(String.format("unsupported class: %s", t.getName()));
      }
      index(index, type, current);
      if (x == midpoint) {
        logger.debug("midpoint: {}", x);
        result = current;
      }
    }

    refreshElasticsearch();
    return (T) result;
  }

  /**
   * This will cause elasticsearch to 'flush' ensuring that queries return correctly.
   * Should be called after every data population.
   */
  private void refreshElasticsearch() {
    refreshElasticsearch(indexForTicks);
    refreshElasticsearch(indexForIndicators);
    refreshElasticsearch(indexForStrategies);
  }

  private void refreshElasticsearch(String index) {
    try {
      RefreshResponse refreshResponse = elasticsearch.admin().indices().prepareRefresh(index).get();
      logger.debug("failed: {}", refreshResponse.getFailedShards());
      logger.debug("succeeded: {}", refreshResponse.getSuccessfulShards());
    } catch (Exception e) {
      logger.debug(e.getMessage());
    }
  }

  /**
   * Index a given object into elasticsearch. The object must be serializable with Jackson.
   *  
   * @param index the elasticserch index to write the object into
   * @param type the type of the object being written
   * @param object the object to write into the index
   * @return the id of the object indexed by elasticsearch
   * @throws ElasticsearchException
   * @throws JsonProcessingException
   */
  private String index(String index, String type, Object object) throws ElasticsearchException, JsonProcessingException {
    IndexResponse response = elasticsearch
        .prepareIndex(index, type)
        .setSource(json.writeValueAsBytes(object))
        .get();
    logger.debug("successfully indexed new object: index:{}, type:{}, id:{}, object:{}",
        response.getIndex(), response.getType(), response.getId(), object);
    return response.getId();
  }

  /**
   * removes all known indexes from the in-process elasticsearch
   *  
   * @param index the elasticsearch index to delete from
   * @param type the type of the object being deleted
   * @param id the id of the object being deleted
   */
  private void deleteElasticsearch(String index) {
    DeleteIndexResponse response = null;
    try {
      response = elasticsearch.admin().indices().prepareDelete(index).get();
      logger.debug("successfully deleted: index:{}, headers:{}", index, response.getHeaders());
    } catch (Exception e) {
      logger.debug(e.getMessage());
    }
  }

}

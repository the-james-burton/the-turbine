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

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.ExchangeEnum;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
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

/**
 * Unfortunately, using elasticsearch >5 in this way to do isolated integration tests is no longer supported.
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/breaking_50_java_api_changes.html
 * 
 * An alternative approach might be to use a standalone elasticsearch which is also possible in travis ci...
 * https://www.peterbe.com/plog/elasticsearch-5-in-travis-ci
 * 
 * Furthermore, note that elasticsearch >5 uses log4j2 and does not work with the log4j-to-slf4j v2, see... 
 * https://github.com/the-james-burton/the-turbine/issues/28
 * 
 * This will mean that unfortunately this ES test will not work in Eclipse, since its JUnit runner
 * does not honor maven surefire exclusions (and eclipse does not seem to have any other way of excluding a dependency).
 *
 * @author the-james-burton
 */
@RunWith(SpringRunner.class)
// @SpringApplicationConfiguration(classes = MockServletContext.class)
@ActiveProfiles("it")
// @Ignore
public class ElasticsearchNativeServiceImplTest extends SpringBootContextLoader {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String elasticsearchTmpDir = "./target/elasticsearch";

  private static final String ricOne = "ABC.L";

  private static final String ricTwo = "DEF.L";

  private static final String ricThree = "GHI.L";

  private static final String ricFour = "JLK.AX";

  private static final String ricFive = "LMN.AX";

  private static final Ticker T1 = Ticker.of(ricOne, "ABCName");

  private static final Ticker T2 = Ticker.of(ricTwo, "DEFName");

  private static final Ticker T3 = Ticker.of(ricThree, "GHIName");

  private static final Ticker T4 = Ticker.of(ricFour, "JKLName");

  private static final Ticker T5 = Ticker.of(ricFive, "KMNName");

  @InjectMocks
  private ElasticsearchNativeServiceImpl service = new ElasticsearchNativeServiceImpl();

  @Mock
  private InfrastructureProperties infrastructureProperties;

  private final DomainObjectGenerator rdogOne = new RandomDomainObjectGenerator(T1);

  private final DomainObjectGenerator rdogTwo = new RandomDomainObjectGenerator(T2);

  private final ObjectMapper json = new ObjectMapper();

  private static Client elasticsearch;

  private static Node node;

  private static final Integer elasticsearchNativePort = 9305;

  private static final Integer elasticsearchRestPort = 9205;

  private static final String elasticsearchCluster = "elasticsearch-test";

  private static final String elasticsearchHost = "localhost";

  private static final String indexForTickers = "turbine-tickers-test";

  private static final String typeForTickers = "turbine-ticker-test";

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
      when(infrastructureProperties.getElasticsearchIndexForTickers()).thenReturn(indexForTickers);
      when(infrastructureProperties.getElasticsearchTypeForTickers()).thenReturn(typeForTickers);
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

  private static class MyNode extends Node {
    public MyNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
      super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins);
    }
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    logger.info("setup()");
    FileUtils.deleteDirectory(new File(elasticsearchTmpDir));

    Settings settings = Settings.builder()
        .put("path.home", elasticsearchTmpDir)
        .put("path.conf", elasticsearchTmpDir)
        .put("path.data", elasticsearchTmpDir)
        // .put("path.work", elasticsearchTmpDir)
        .put("path.logs", elasticsearchTmpDir)
        .put("transport.type", "netty4")
        .put("transport.tcp.port", elasticsearchNativePort)
        .put("http.type", "netty4")
        .put("http.enabled", "true")
        .put("http.port", elasticsearchRestPort)
        .put("cluster.name", elasticsearchCluster)
        // .put("index.number_of_shards", "1")
        // .put("index.number_of_replicas", "0")
        // .put("discovery.zen.ping.multicast.enabled", "false")
        .build();

    Collection<Class<? extends Plugin>> plugins = Arrays.asList(Netty4Plugin.class);
    node = new MyNode(settings, plugins);
    // node.
    // .data(true)
    // .client(false)
    // .settings(settings)
    // .clusterName(elasticsearchCluster)
    // .node();
    node.start();
    elasticsearch = node.client();
  }

  @After
  public void tearDown() {
    logger.debug("tearDown()");
    // delete whole index, not each id...
    deleteElasticsearch(indexForTickers);
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
    String result = service.findTicks();
    logger.info(" *** getAllTicks(): {}", result);
    @SuppressWarnings("unchecked")
    List<TickJson> ticks = (List<TickJson>) json.readValue(result, List.class);
    logger.info("expected:{}, actual:{}", number, ticks.size());
    assertThat(ticks).hasSize(number);
    assertThat(result).contains("timestamp");
  }

  @Test
  public void testFindTicksByRic() throws Exception {
    int numberOne = 5;
    int numberTwo = 7;
    logger.info("given {} {} ticks and {} {} ticks",
        numberOne, ricOne, numberTwo, ricTwo);
    populateElasticsearch(rdogOne, numberOne, indexForTicks, typeForTicks, null, TickJson.class);
    populateElasticsearch(rdogTwo, numberTwo, indexForTicks, typeForTicks, null, TickJson.class);
    logger.info("it should return {} {} ticks, {} {} ticks and 0 {} ticks",
        numberOne, ricOne, numberTwo, ricTwo, ricThree);
    List<TickJson> resultOne = service.findTicksByRic(ricOne);
    List<TickJson> resultTwo = service.findTicksByRic(ricTwo);
    List<TickJson> resultThree = service.findTicksByRic(ricThree);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        ricOne, resultOne.size(), numberOne);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        ricTwo, resultTwo.size(), numberTwo);
    logger.info(" *** findTicksByTicker({}): {}, expected: {}",
        ricThree, resultThree.size(), 0);
    assertThat(resultOne).hasSize(numberOne);
    assertThat(resultTwo).hasSize(numberTwo);
    assertThat(resultThree).hasSize(0);
  }

  @Test
  public void testFindIndicatorsByTicker() throws Exception {
    int numberOne = 5;
    int numberTwo = 7;
    logger.info("given {} {} indicators and {} {} indicators",
        numberOne, ricOne, numberTwo, ricTwo);
    String name = "indicator-name";
    populateElasticsearch(rdogOne, numberOne, indexForIndicators, typeForIndicators, name, IndicatorJson.class);
    populateElasticsearch(rdogTwo, numberTwo, indexForIndicators, typeForIndicators, name, IndicatorJson.class);
    logger.info("it should return {} {} indictors, {} {} indicators and 0 {} indicators",
        numberOne, ricOne, numberTwo, ricTwo, ricThree);
    List<IndicatorJson> resultOne = service.findIndicatorsByRic(ricOne);
    List<IndicatorJson> resultTwo = service.findIndicatorsByRic(ricTwo);
    List<IndicatorJson> resultThree = service.findIndicatorsByRic(ricThree);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        ricOne, resultOne.size(), numberOne);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        ricTwo, resultTwo.size(), numberTwo);
    logger.info(" *** findIndicatorsByTicker({}): {}, expected: {}",
        ricThree, resultThree.size(), 0);
    assertThat(resultOne).hasSize(numberOne);
    assertThat(resultTwo).hasSize(numberTwo);
    assertThat(resultThree).hasSize(0);
  }

  @Test
  public void testFindTicksByTickerAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 4;
    logger.info("given {} {} ticks", number, ricOne);
    long midpoint = populateElasticsearch(rdogOne, number, indexForTicks, typeForTicks, null, TickJson.class).getDate();
    logger.info("it should return only {} {} ticks after the midpoint", expected, ricOne);
    List<TickJson> result = service.findTicksByRicAndDateGreaterThan(ricOne, midpoint);
    logger.info(" *** findTicksByTickerAndDateGreaterThan({}, {}): {}, expected: {}",
        ricOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  @Test
  public void testFindIndicatorsByTickerAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 4;
    logger.info("given {} {} indicators", number, ricOne);
    long midpoint = populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, null,
        IndicatorJson.class).getDate();
    logger.info("it should return only {} {} indicators after the midpoint", expected, ricOne);
    List<IndicatorJson> result = service.findIndicatorsByRicAndDateGreaterThan(ricOne, midpoint);
    logger.info(" *** findIndicatorsByTickerAndDateGreaterThan({}, {}): {}, expected: {}",
        ricOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  @Test
  public void testFindIndicatorsByTickerAndNameAndDateGreaterThan() throws Exception {
    int number = 7;
    int expected = 7;
    final String nameOne = "indicator-one";
    final String nameTwo = "indicator-two";
    logger.info("given {} {} {} indicators and {} {} {} indicators",
        number, ricOne, nameOne, number, ricOne, nameTwo);
    populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, nameOne, IndicatorJson.class);
    long midpoint = OffsetDateTime.now().toInstant().toEpochMilli();
    populateElasticsearch(rdogOne, number, indexForIndicators, typeForIndicators, nameTwo, IndicatorJson.class);
    logger.info("it should return only {} {} {} indicators after the midpoint", expected, nameOne, ricOne);
    List<IndicatorJson> result = service.findIndicatorsByRicAndNameAndDateGreaterThan(ricOne, nameOne, midpoint);
    logger.info(" *** findIndicatorsByTickerAndNameAndDateGreaterThan({}, {}, {}): {}, expected: {}",
        ricOne, nameOne, midpoint, result.size(), expected);
    assertThat(result).hasSize(expected);
  }

  @Test
  public void testFindTickersByExchange() throws Exception {
    ExchangeEnum exchangeOne = ExchangeEnum.LSE;
    ExchangeEnum exchangeTwo = ExchangeEnum.ASX;
    logger.info("given the following tickers: {} {} {} {}",
        ricOne, ricTwo, ricFour, ricFive);
    index(indexForTickers, typeForTickers, T1);
    index(indexForTickers, typeForTickers, T2);
    index(indexForTickers, typeForTickers, T4);
    index(indexForTickers, typeForTickers, T5);
    refreshElasticsearch();

    logger.info("when asking for {}, it should return {}",
        exchangeOne, Arrays.asList(ricOne, ricTwo));
    List<Ticker> resultOne = service.findTickersByExchange(exchangeOne);
    logger.info(" *** findTickersByExchange({}): {}, expected: {}",
        exchangeOne, resultOne.stream().map(t -> t.getRicAsString()).collect(toList()), Arrays.asList(ricOne, ricTwo));
    assertThat(resultOne).hasSize(2);
    assertThat(resultOne).containsExactlyInAnyOrder(T1, T2);

    logger.info("when asking for {}, it should return {}",
        exchangeTwo, Arrays.asList(ricFour, ricFive));
    List<Ticker> resultTwo = service.findTickersByExchange(exchangeTwo);
    logger.info(" *** findTickersByExchange({}): {}, expected: {}",
        exchangeTwo, resultTwo.stream().map(t -> t.getRicAsString()).collect(toList()), Arrays.asList(ricFour, ricFive));
    assertThat(resultTwo).hasSize(2);
    assertThat(resultTwo).containsExactlyInAnyOrder(T4, T5);
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
    refreshElasticsearch(indexForTickers);
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
    // logger.debug("indexing: index:{}, type:{}, object:{}", index, type, object);
    IndexResponse response = elasticsearch
        .prepareIndex(index, type)
        .setSource(json.writeValueAsBytes(object))
        .get();
    logger.debug("successfully indexed: index:{}, type:{}, id:{}, object:{}",
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
      logger.debug("successfully deleted: index:{}, isAcknowledged:{}", index, response.isAcknowledged());
    } catch (Exception e) {
      logger.debug(e.getMessage());
    }
  }

}

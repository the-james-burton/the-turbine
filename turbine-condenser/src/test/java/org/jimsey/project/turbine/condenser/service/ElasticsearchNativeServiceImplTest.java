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
package org.jimsey.project.turbine.condenser.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.qpid.util.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.service.ElasticsearchNativeServiceImpl;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@ActiveProfiles("it")
// @Ignore
public class ElasticsearchNativeServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String elasticsearchTmpDir = "./target/elasticsearch";

  private static final String market = "FTSE100";

  private static final String stockOne = "ABC";

  private static final String StockTwo = "DEF";

  @InjectMocks
  private ElasticsearchNativeServiceImpl service = new ElasticsearchNativeServiceImpl();

  @Mock
  private InfrastructureProperties infrastructureProperties;

  private final DomainObjectGenerator rdogOne = new RandomDomainObjectGenerator(market, stockOne);

  private final DomainObjectGenerator rdogTwo = new RandomDomainObjectGenerator(market, StockTwo);

  private final ObjectMapper json = new ObjectMapper();

  private static Client elasticsearch;

  private static Node node;

  private static final Integer elasticsearchNativePort = 9305;

  private static final Integer elasticsearchRestPort = 9205;

  private static final String elasticsearchCluster = "elasticsearch-test";

  private static final String elasticsearchHost = "localhost";

  private static final String indexForTicks = "turbine-ticks-test";

  private static final String typeForTicks = "turbine-tick-test";

  private boolean initialised = false;

  private final Set<String> ids = new HashSet<>();

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

      // TODO should I expect this @PostConstruct be called automatically for me?
      service.init();
      initialised = true;
    }

  }

  private void addTicks(DomainObjectGenerator rdog, int numberOfTicks) throws JsonProcessingException, InterruptedException {
    int x = 0;
    while (++x <= numberOfTicks) {
      ids.add(indexTick());
    }
    // TODO too quick and the ES search returns nothing! can we flush ES?
    Thread.sleep(1000);
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    logger.info("setup()");
    FileUtils.delete(new File(elasticsearchTmpDir), true);

    Settings settings = ImmutableSettings.builder()
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
    logger.info("tearDown()");
    ids.stream().forEach(id -> deleteTick(id));
  }

  @Test
  public void testGetAllticks() throws Exception {
    int numberOfTicks = 12;
    logger.info("given any {} ticks");
    addTicks(rdogOne, 12);
    logger.info("it should return all ticks");
    String result = service.getAllTicks();
    logger.info(" *** getAllTicks(): {}", result);
    @SuppressWarnings("unchecked")
    List<TickJson> ticks = (List<TickJson>) json.readValue(result, List.class);
    logger.info("expected:{}, actual:{}", numberOfTicks, ticks.size());
    assertThat(ticks, hasSize(numberOfTicks));
    assertThat(result, containsString("timestamp"));
  }

  // ------------------------------------------------------
  private String indexTick() throws ElasticsearchException, JsonProcessingException {
    IndexResponse response = elasticsearch
        .prepareIndex(
            indexForTicks,
            typeForTicks)
        .setSource(json.writeValueAsBytes(rdogOne.newTick()))
        .get();
    logger.debug("successfully indexed new tick: index:{}, type:{}, id:{}",
        response.getIndex(), response.getType(), response.getId());
    return response.getId();
  }

  private void deleteTick(String id) {
    DeleteResponse response = elasticsearch
        .prepareDelete(
            indexForTicks,
            typeForTicks,
            id)
        .get();
    logger.debug("successfully deleted tick: index:{}, type:{}, id:{}",
        response.getIndex(), response.getType(), response.getId());
  }

}

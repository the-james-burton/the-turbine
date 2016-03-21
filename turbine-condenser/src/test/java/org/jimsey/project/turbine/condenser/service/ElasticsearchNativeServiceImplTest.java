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

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;
import org.jimsey.projects.turbine.condenser.component.InfrastructureProperties;
import org.jimsey.projects.turbine.condenser.service.ElasticsearchNativeServiceImpl;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
@Ignore
public class ElasticsearchNativeServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @InjectMocks
  private ElasticsearchNativeServiceImpl service = new ElasticsearchNativeServiceImpl();

  @Mock
  private InfrastructureProperties infrastructureProperties;

  private final DomainObjectGenerator rdog = new RandomDomainObjectGenerator("FTSE100", "ABC");

  private final ObjectMapper json = new ObjectMapper();

  private final Client elasticsearch;

  private final int testTicks = 10;

  private final String indexForTicks = "turbine-ticks-test";

  private final String typeForTicks = "turbine-tick-test";

  private final Set<String> ids = new HashSet<>();

  public ElasticsearchNativeServiceImplTest() {
    elasticsearch = NodeBuilder.nodeBuilder().clusterName("elasticsearch").client(true).node().client();
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(infrastructureProperties.getElasticsearchHost()).thenReturn("localhost");
    Mockito.when(infrastructureProperties.getElasticsearchNativePort()).thenReturn(9300);
    Mockito.when(infrastructureProperties.getElasticsearchIndexForTicks()).thenReturn(indexForTicks);
    // TODO should I expect this @PostConstruct be called automaticaly for me?
    service.init();
    int x = 0;
    while (++x < testTicks) {
      ids.add(indexTick());
    }
  }

  @After
  public void clearDown() {
    ids.stream().forEach(id -> deleteTick(id));
  }

  private String indexTick() throws ElasticsearchException, JsonProcessingException {
    IndexResponse response = elasticsearch
        .prepareIndex(
            indexForTicks,
            typeForTicks)
        .setSource(json.writeValueAsBytes(rdog.newTick()))
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

  @Test
  public void testGetAllticks() {
    logger.info("it should return all ticks");
  }

}

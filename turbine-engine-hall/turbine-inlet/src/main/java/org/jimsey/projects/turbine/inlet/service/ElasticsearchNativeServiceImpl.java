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
package org.jimsey.projects.turbine.inlet.service;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jimsey.projects.turbine.inlet.external.domain.Company;
import org.jimsey.projects.turbine.inlet.external.domain.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchNativeServiceImpl implements ElasticsearchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${elasticsearch.cluster}")
  private String cluster;

  @Value("${elasticsearch.host}")
  private String host;

  @Value("${elasticsearch.port}")
  private Integer port;

  @Value("${elasticsearch.index.company}")
  private String indexForCompany;

  @Value("${elasticsearch.type.company}")
  private String typeForCompany;

  @Value("${elasticsearch.index.security}")
  private String indexForSecurity;

  @Value("${elasticsearch.type.security}")
  private String typeForSecurity;

  private TransportClient elasticsearch;

  @PostConstruct
  public void init() {
    Settings settings = Settings.builder()
        .put("cluster.name", cluster)
        .put("transport.type", "netty4")
        .build();
    elasticsearch = new PreBuiltTransportClient(settings);
    logger.info(" *** connecting to : {}:{}:{}", cluster, host, port);

    InetSocketAddress address = new InetSocketAddress(host, port);
    InetSocketTransportAddress transport = new InetSocketTransportAddress(address);
    elasticsearch.addTransportAddress(transport);
  }

  @Override
  public String indexCompany(Company company) {
    logger.info("indexCompany:{}", company.toString());
    IndexResponse response = elasticsearch
        .prepareIndex(indexForCompany, typeForCompany)
        .setSource(company.toString())
        .get();
    return response.toString();
  }

  @Override
  public String indexSecurity(Security security) {
    logger.info("indexSecurity:{}", security.toString());
    IndexResponse response = elasticsearch
        .prepareIndex(indexForSecurity, typeForSecurity)
        .setSource(security.toString())
        .get();
    return response.toString();
  }

}

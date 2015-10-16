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
package org.jimsey.projects.turbine.spring.camel.routes;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.camel.builder.RouteBuilder;
import org.jimsey.projects.turbine.spring.component.InfrastructureProperties;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRoute extends RouteBuilder {

  private final String index;

  private final String type;

  private String elasticsearchUri;

  @Autowired
  @NotNull
  protected InfrastructureProperties infrastructureProperties;

  public BaseRoute(String index, String type) {
    this.index = index;
    this.type = type;
  }

  @PostConstruct
  public void init() {
    elasticsearchUri = String.format(
        "elasticsearch://elasticsearch?ip=%s&port=%s&operation=INDEX&indexName=%s&indexType=%s",
        infrastructureProperties.getElasticsearchHost(),
        infrastructureProperties.getElasticsearchPort(),
        index, type);
  }

  public abstract String getWebsocket();

  @Override
  public void configure() throws Exception {
    // if required
  }

  // ------------------------------
  public String getIndex() {
    return index;
  }

  public String getType() {
    return type;
  }

  public String getElasticsearchUri() {
    return elasticsearchUri;
  }

}

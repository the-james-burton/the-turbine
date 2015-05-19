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
package org.jimsey.projects.turbine.spring.elasticsearch.repositories.custom;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.spring.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformationCreator;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformationCreatorImpl;

public abstract class EntityRepositoryImpl<T extends Entity> implements EntityRepositoryCustom<T> {

  private static final Logger logger = LoggerFactory.getLogger(EntityRepositoryImpl.class);

  @Autowired
  @NotNull
  ElasticsearchOperations elasticsearch;

  ElasticsearchEntityInformation<T, Long> information;

  private Class<T> myEntity;

  @PostConstruct
  @SuppressWarnings("unchecked")
  public void init() throws ClassNotFoundException {
    ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    myEntity = (Class<T>) type.getActualTypeArguments()[0];
    ElasticsearchEntityInformationCreator informationCreator = new ElasticsearchEntityInformationCreatorImpl(elasticsearch
        .getElasticsearchConverter().getMappingContext());
    information = informationCreator.getEntityInformation(myEntity);
    logger.info("{} repository initialized", information.getIndexName());
  }

  @Override
  public void saveToIndex(T entity, String indexName) {
    logger.info(" .... saveToIndex {} : {}", this.getClass().getName(), entity.getClass().getName());
    IndexQuery query = createIndexQuery(entity);
    query.setIndexName(indexName);
    elasticsearch.index(query);
  }

  private IndexQuery createIndexQuery(T entity) {
    IndexQuery query = new IndexQuery();
    query.setObject(entity);
    query.setId(information.getId(entity).toString());
    query.setVersion(information.getVersion(entity));
    query.setParentId(information.getParentId(entity));
    return query;
  }

}

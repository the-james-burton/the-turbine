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
package org.jimsey.projects.turbine.spring.camel.splitters;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.jimsey.projects.camel.components.SpringSimpleMessagingConstants;
import org.jimsey.projects.turbine.spring.TurbineConstants;
import org.jimsey.projects.turbine.spring.component.MarketsManager;
import org.jimsey.projects.turbine.spring.domain.Entity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseSplitter {

  protected static final Logger logger = LoggerFactory.getLogger(BaseSplitter.class);

  @Autowired
  @NotNull
  protected MarketsManager marketsManager;

  protected Message createMessage(Entity entity, Map<String, Object> headers) {
    DefaultMessage message = new DefaultMessage();
    message.setHeaders(headers);
    message.setBody(entity);
    message.setHeader(TurbineConstants.HEADER_FOR_OBJECT_TYPE, entity.getClass().getName());
    message.setHeader(SpringSimpleMessagingConstants.DESTINATION_SUFFIX,
        String.format(".%s.%s", entity.getMarket(), entity.getSymbol()));

    logger.info("entity: [body: {}, headers: {}]", entity.toString(), new JSONObject(headers));

    return message;
  }

}

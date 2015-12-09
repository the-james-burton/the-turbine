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
package org.jimsey.projects.turbine.fuel.camel;

import java.io.IOException;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.jimsey.projects.turbine.fuel.domain.Entity;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;

import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class TurbineObjectConverter {

  private static ObjectMapper json = new ObjectMapper();

  // --------------------------------------------
  @Converter
  public static byte[] toBytes(final Entity entity, final Exchange exchange) {
    return SerializationUtils.serialize(entity);
  }

  // --------------------------------------------
  @Converter
  public static String toString(final TickJson tick, final Exchange exchange) {
    return tick.toString();
  }

  @Converter
  public static String toString(final IndicatorJson indicator, final Exchange exchange) {
    return indicator.toString();
  }

  @Converter
  public static String toString(final StrategyJson strategy, final Exchange exchange) {
    return strategy.toString();
  }

  // --------------------------------------------
  @Converter
  public static TickJson toTickJson(final String text, final Exchange exchange) {
    TickJson tick = null;
    try {
      tick = json.readValue(text, TickJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tick;
  }

  @Converter
  public static TickJson toTickJson(final byte[] bytes, final Exchange exchange) {
    TickJson tick = null;
    try {
      tick = json.readValue(bytes, TickJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tick;
  }

  @Converter
  public static IndicatorJson toIndicatorJson(final String text, final Exchange exchange) {
    IndicatorJson indicator = null;
    try {
      indicator = json.readValue(text, IndicatorJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return indicator;
  }

  @Converter
  public static StrategyJson toStrategyJson(final String text, final Exchange exchange) {
    StrategyJson strategy = null;
    try {
      strategy = json.readValue(text, StrategyJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return strategy;
  }

}
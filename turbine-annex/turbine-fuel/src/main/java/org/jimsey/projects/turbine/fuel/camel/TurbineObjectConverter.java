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

import org.apache.commons.lang3.SerializationUtils;
import org.jimsey.projects.turbine.fuel.domain.Entity;
import org.jimsey.projects.turbine.fuel.domain.IndicatorJson;
import org.jimsey.projects.turbine.fuel.domain.StrategyJson;
import org.jimsey.projects.turbine.fuel.domain.TickJson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This was used by camel. I am leaving it in for now in case the methods are useful
 * for spring AMQP or elasticsearch message conversion...
 *
 * @author the-james-burton
 */
@Deprecated
public class TurbineObjectConverter {

  private static ObjectMapper json = new ObjectMapper();

  // --------------------------------------------
  public static byte[] toBytes(final Entity entity) {
    return SerializationUtils.serialize(entity);
  }

  // --------------------------------------------
  public static String toString(final TickJson tick) {
    return tick.toString();
  }

  public static String toString(final IndicatorJson indicator) {
    return indicator.toString();
  }

  public static String toString(final StrategyJson strategy) {
    return strategy.toString();
  }

  // --------------------------------------------
  public static TickJson toTickJson(final String text) {
    TickJson tick = null;
    try {
      tick = json.readValue(text, TickJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tick;
  }

  public static TickJson toTickJson(final byte[] bytes) {
    TickJson tick = null;
    try {
      tick = json.readValue(bytes, TickJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tick;
  }

  public static IndicatorJson toIndicatorJson(final String text) {
    IndicatorJson indicator = null;
    try {
      indicator = json.readValue(text, IndicatorJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return indicator;
  }

  public static StrategyJson toStrategyJson(final String text) {
    StrategyJson strategy = null;
    try {
      strategy = json.readValue(text, StrategyJson.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return strategy;
  }

}
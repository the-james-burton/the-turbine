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
package org.jimsey.projects.turbine.spring.camel.typeconverters;

import java.io.IOException;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jimsey.projects.turbine.spring.domain.Entity;
import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.domain.Quote;
import org.jimsey.projects.turbine.spring.domain.TickJson;
import org.jimsey.projects.turbine.spring.domain.Trade;
import org.jimsey.projects.turbine.spring.domain.Trader;
import org.springframework.util.SerializationUtils;

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
  public static Quote toQuote(final byte[] bytes, final Exchange exchange) {
    return (Quote) SerializationUtils.deserialize(bytes);
  }

  @Converter
  public static Instrument toInstrument(final byte[] bytes, final Exchange exchange) {
    return (Instrument) SerializationUtils.deserialize(bytes);
  }

  @Converter
  public static Trade toTrade(final byte[] bytes, final Exchange exchange) {
    return (Trade) SerializationUtils.deserialize(bytes);
  }

  @Converter
  public static Trader toTrader(final byte[] bytes, final Exchange exchange) {
    return (Trader) SerializationUtils.deserialize(bytes);
  }

  // --------------------------------------------
  @Converter
  public static String toString(final Quote quote, final Exchange exchange) {
    return quote.toString();
  }

  @Converter
  public static String toString(final Instrument instrument, final Exchange exchange) {
    return instrument.toString();
  }

  @Converter
  public static String toString(final Trade trade, final Exchange exchange) {
    return trade.toString();
  }

  @Converter
  public static String toString(final Trader trader, final Exchange exchange) {
    return trader.toString();
  }

  @Converter
  public static String toString(final TickJson tick, final Exchange exchange) {
    return tick.toString();
  }

  // --------------------------------------------
  @Converter
  public static Quote toQuote(final String text, final Exchange exchange) {
    Quote quote = null;
    try {
      quote = json.readValue(text, Quote.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return quote;
  }

  @Converter
  public static Instrument toInstrument(final String text, final Exchange exchange) {
    Instrument instrument = null;
    try {
      instrument = json.readValue(text, Instrument.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return instrument;
  }

  @Converter
  public static Trade toTrade(final String text, final Exchange exchange) {
    Trade trade = null;
    try {
      trade = json.readValue(text, Trade.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return trade;
  }

  @Converter
  public static Trader toTrader(final String text, final Exchange exchange) {
    Trader trader = null;
    try {
      trader = json.readValue(text, Trader.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return trader;
  }

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

}
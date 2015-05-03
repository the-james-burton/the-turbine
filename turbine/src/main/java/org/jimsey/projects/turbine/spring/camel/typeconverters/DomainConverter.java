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

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jimsey.projects.turbine.spring.domain.Entity;
import org.jimsey.projects.turbine.spring.domain.Instrument;
import org.jimsey.projects.turbine.spring.domain.Quote;
import org.jimsey.projects.turbine.spring.domain.Trade;
import org.jimsey.projects.turbine.spring.domain.Trader;
import org.springframework.util.SerializationUtils;

@Converter
public class DomainConverter {


  @Converter
  public static byte[] toBytes(Entity entity, Exchange exchnge) {
    return SerializationUtils.serialize(entity);
  }

  @Converter
  public static byte[] toBytes(Instrument instrument, Exchange exchnge) {
    return SerializationUtils.serialize(instrument);
  }

  @Converter
  public static byte[] toBytes(Quote quote, Exchange exchnge) {
    return SerializationUtils.serialize(quote);
  }

  @Converter
  public static byte[] toBytes(Trade trade, Exchange exchnge) {
    return SerializationUtils.serialize(trade);
  }

  @Converter
  public static byte[] toBytes(Trader trader, Exchange exchnge) {
    return SerializationUtils.serialize(trader);
  }

  // --------------------------------------------
  @Converter
  public static Entity toEntity(byte[] bytes, Exchange exchnge) {
    return (Entity) SerializationUtils.deserialize(bytes);
  }
  
  @Converter
  public static Instrument toInstrument(byte[] bytes, Exchange exchnge) {
    return (Instrument) SerializationUtils.deserialize(bytes);
  }
 
  @Converter
  public static Quote toQuote(byte[] bytes, Exchange exchnge) {
    return (Quote) SerializationUtils.deserialize(bytes);
  }
 
  @Converter
  public static Trade toTrade(byte[] bytes, Exchange exchnge) {
    return (Trade) SerializationUtils.deserialize(bytes);
  }
 
  @Converter
  public static Trader toTrader(byte[] bytes, Exchange exchnge) {
    return (Trader) SerializationUtils.deserialize(bytes);
  }
 
}
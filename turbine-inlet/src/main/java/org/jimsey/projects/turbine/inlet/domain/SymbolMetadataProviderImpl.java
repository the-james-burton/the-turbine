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
package org.jimsey.projects.turbine.inlet.domain;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class SymbolMetadataProviderImpl implements SymbolMetadataProvider {

  Map<String, SymbolMetadata> metadata = Maps.newHashMap();
  
  // TODO make this return/lookup real data...
  public SymbolMetadataProviderImpl() {
    metadata.put("ABC.L", new SymbolMetadata("ABC", "ABCName", Market.FTSE100));
    metadata.put("DEF.L", new SymbolMetadata("DEF", "DEFName", Market.FTSE100));
  }

  
  @Override
  public SymbolMetadata findMetadataForTicker(String ticker) {
    return metadata.get(ticker);
  }

  @Override
  public SymbolMetadata findMetadataForMarketAndSymbol(String market, String symbol) {
    String ticker = String.format("%s%s", symbol, Market.valueOf(market).getExtension());
    return findMetadataForTicker(ticker);
  }

}

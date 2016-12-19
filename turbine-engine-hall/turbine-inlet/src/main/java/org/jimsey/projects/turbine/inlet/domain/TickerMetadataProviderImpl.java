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
package org.jimsey.projects.turbine.inlet.domain;

import java.util.HashMap;
import java.util.Map;

import org.jimsey.projects.turbine.fuel.domain.MarketEnum;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.TickerMetadata;
import org.springframework.stereotype.Service;

import javaslang.control.Option;

@Service
public class TickerMetadataProviderImpl implements TickerMetadataProvider {

  Map<Ticker, TickerMetadata> metadata = new HashMap<>();
  
  // TODO make this return/lookup real data...
  public TickerMetadataProviderImpl() {
    Ticker ABC_L = Ticker.of("ABC.L");
    Ticker DEF_L = Ticker.of("DEF.L");
    addMetadata(TickerMetadata.of(ABC_L, "ABCName"));
    addMetadata(TickerMetadata.of(DEF_L, "DEFName"));
  }

  @Override
  public void addMetadata(TickerMetadata tickerMetadata) {
    metadata.put(tickerMetadata.getTicker(), tickerMetadata);
  }
  
  @Override
  public Option<TickerMetadata> findMetadataForTicker(Ticker ticker) {
    return Option.of(metadata.get(ticker));
  }

  @Override
  public Option<TickerMetadata> findMetadataForTicker(String ticker) {
    return Option.of(metadata.get(Ticker.of(ticker)));
  }

  @Override
  public Option<TickerMetadata> findMetadataForMarketAndSymbol(String market, String symbol) {
    String ticker = String.format("%s.%s", symbol, MarketEnum.valueOf(market).getExtension());
    return findMetadataForTicker(ticker);
  }

}

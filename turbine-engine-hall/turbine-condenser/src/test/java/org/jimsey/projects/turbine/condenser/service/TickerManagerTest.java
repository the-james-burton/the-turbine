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
package org.jimsey.projects.turbine.condenser.service;

import static org.assertj.core.api.Assertions.*;

import java.lang.invoke.MethodHandles;

import org.jimsey.projects.turbine.condenser.domain.Stock;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.collection.HashSet;

public class TickerManagerTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @InjectMocks
  private TickerManager tickerManager;

  private final Ticker ABC_L = Ticker.of("ABC.L");
  private final Ticker DEF_L = Ticker.of("DEF.L");

  // private StockFactory stockFactory = new StockFactory() {
  // public Stock createStock(Ticker ticker) {
  // return Stock.of(ticker, new ArrayList<>(), new ArrayList<>());
  // };
  // };

  @Before
  public void setup() {
  }

  @Test
  public void testAddTick() {
    logger.info("given an empty TickerManager");
    tickerManager = new TickerManager();
    // tickerManager.setStockFactory(stockFactory);
    logger.info("then it should be emtpy...");
    assertThat(tickerManager.getTickers()).isEmpty();

    logger.info("when adding ticker ABC.L");
    Stock stockABC_L = tickerManager.findOrCreateStock(ABC_L);
    logger.info("then a stock with ticker ABC.L should be returned...");
    assertThat(stockABC_L.getTicker()).isEqualTo(ABC_L);
    logger.info("then getTickers() should contain ticker ABC.L...");
    assertThat(tickerManager.getTickers()).contains(ABC_L);
    logger.info("and getTickers() should be of size 1...");
    assertThat(tickerManager.getTickers()).hasSize(1);
    logger.info("and getTickers() should be equal to a set containing ticker ABC.L...");
    assertThat(tickerManager.getTickers()).isEqualTo(HashSet.of(ABC_L));

    logger.info("when adding ticker ABC.L again");
    tickerManager.findOrCreateStock(ABC_L);
    logger.info("then getTickers() should still contain ticker ABC.L...");
    assertThat(tickerManager.getTickers()).contains(ABC_L);
    logger.info("and getTickers() should still be of size 1...");
    assertThat(tickerManager.getTickers()).hasSize(1);
    logger.info("and getTickers() should be still equal to a set containing ticker ABC.L...");
    assertThat(tickerManager.getTickers()).isEqualTo(HashSet.of(ABC_L));

    logger.info("when adding ticker DEF.L");
    Stock stockDEF_L = tickerManager.findOrCreateStock(DEF_L);
    logger.info("then a stock with ticker DEF.L should be returned...");
    assertThat(stockDEF_L.getTicker()).isEqualTo(DEF_L);
    logger.info("then getTickers() should contain ticker DEF.L...");
    assertThat(tickerManager.getTickers()).contains(DEF_L);
    logger.info("and getTickers() should now be of size 2...");
    assertThat(tickerManager.getTickers()).hasSize(2);
    logger.info("and getTickers() should be equal to a set containing ticker ABC.L and DEF.L...");
    assertThat(tickerManager.getTickers()).isEqualTo(HashSet.of(ABC_L, DEF_L));

  }

}

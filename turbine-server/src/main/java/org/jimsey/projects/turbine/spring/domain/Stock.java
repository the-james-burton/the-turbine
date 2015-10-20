package org.jimsey.projects.turbine.spring.domain;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.BollingerBandsUpperIndicator;

public class Stock {

  private static final Logger logger = LoggerFactory.getLogger(Stock.class);

  private final int timeFrame = 10;

  private String symbol;

  private String market;

  private TickJson tick;

  private StockJson stock;

  private final TimeSeries series = new TimeSeries(new ArrayList<Tick>());

  private final ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

  private final SMAIndicator smaIndicator = new SMAIndicator(closePriceIndicator, timeFrame);

  private final StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(smaIndicator, timeFrame);

  private final BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(
      smaIndicator);

  private final BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(
      bollingerBandsMiddleIndicator, standardDeviationIndicator);

  private final BollingerBandsUpperIndicator bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(
      bollingerBandsMiddleIndicator, standardDeviationIndicator);

  public Stock(final String market, final String symbol) {
    this.market = market;
    this.symbol = symbol;
  }

  public void receiveTick(TickJson tick) {
    this.tick = tick;
    logger.debug("market: {}, symbol: {}, receiveTick: {}", market, symbol, tick.getTimestamp());
    series.addTick(tick);
    createStock();
  }

  private void createStock() {
    Double cpi = closePriceIndicator.getValue(series.getEnd()).toDouble();
    Double bbmi = bollingerBandsMiddleIndicator.getValue(series.getEnd()).toDouble();
    Double bbli = bollingerBandsLowerIndicator.getValue(series.getEnd()).toDouble();
    Double bbui = bollingerBandsUpperIndicator.getValue(series.getEnd()).toDouble();
    stock = new StockJson(tick.getDate(), cpi, bbmi, bbli, bbui, symbol, market, tick.getTimestamp());
  }

  public StockJson getStock() {
    return stock;
  }
}

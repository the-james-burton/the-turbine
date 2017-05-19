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
package org.jimsey.projects.turbine.inlet.web;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceHistoric;
import org.jimsey.projects.turbine.fuel.domain.YahooFinanceRealtime;
import org.jimsey.projects.turbine.inlet.domain.DogKennel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

/**
 * http://finance.yahoo.com/d/quotes.csv?f=nxohgav&amp;s=BHP.AX+BLT.L+AAPL
 * http://localhost:48006/finance/yahoo/realtime/ABC
 * 
 * "BHP BLT FPO","ASX",23.20,23.40,23.00,23.31,9714517
 * "BHP BILLITON PLC ORD $0.50","LSE",1225.00,1255.00,1222.00,1232.50,8947122
 * "Apple Inc.","NMS",114.43,114.56,113.51,113.87,13523517
 * 
 * http://real-chart.finance.yahoo.com/table.csv?s=DGE.L&amp;d=6&amp;e=5&amp;f=2016&amp;g=d&amp;a=6&amp;b=1&amp;c=1900&amp;ignore=.csv
 * 
 * Date,Open,High,Low,Close,Volume,Adj Close
 * 2016-07-05,165.40,166.70,158.50,158.50,30017600,158.50
 * 2016-07-04,171.80,172.458,166.20,167.50,19955300,167.50
 * 2016-07-01,173.40,176.70,165.337,169.70,38748700,169.70
 * 
 */
@Controller
@RequestMapping("/finance")
public class FinanceController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private DogKennel kennel;

  // http://finance.yahoo.com/d/quotes.csv?f=nxohgav&s=BHP.AX+BLT.L+AAPL
  // http://real-chart.finance.yahoo.com/table.csv?s=DGE.L&d=6&e=5&f=2016&g=d&a=6&b=1&c=1900&ignore=.csv

  /** 
   * @param tickersString a '+' separated list of tickers with extension, e.g. ABC.L+DEF.L 
   * @return an attachment containing a CSV string of name,exchange,open,high,low,close,volume
   */
  @RequestMapping("/yahoo/realtime/{tickersString:.+}")
  public ResponseEntity<String> yahooFinanceRealtime(@PathVariable @NotNull String tickersString) {
    logger.info("yahooFinanceRealtime({})", tickersString);
    CharSeq results = doNextTickForYahooFinanceRealtime(kennel.parseTickersString.apply(tickersString));

    // create the return type required by this mock API...
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("file", "quotes.csv");
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

  /** 
   * @param tickersString a '+' separated list of tickers with extension, e.g. ABC.L+DEF.L 
   * @return a CSV string of name,exchange,open,high,low,close,volume
   */
  @RequestMapping("/yahoo/realtime/direct/{tickersString:.+}")
  public ResponseEntity<String> yahooFinanceRealtimeDirect(@PathVariable @NotNull String tickersString) {
    logger.info("yahooFinanceRealtimeDirect({})", tickersString);
    CharSeq results = doNextTickForYahooFinanceRealtime(kennel.parseTickersString.apply(tickersString));

    // create the return type required by this mock API...
    HttpHeaders headers = new HttpHeaders();
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

  /** 
   * @param tickersString a ticker with extension, e.g. ABC.L
   * @return a CSV string with header row of Date,Open,High,Low,Close,Volume,Adj Close
   */
  @RequestMapping("/yahoo/historic/{ticker:.+}")
  public ResponseEntity<String> yahooFinanceHistoric(@PathVariable @NotNull String ticker) {
    logger.info("yahooFinanceHistoricDirect({})", ticker);
    CharSeq results = doTicksYahooFinanceHistoric(kennel.parseTickersString.apply(ticker).head());

    // create the return type required by this mock API...
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("file", "quotes.csv");
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

  /** 
   * @param tickersString a ticker with extension, e.g. ABC.L
   * @return a CSV string with header row of Date,Open,High,Low,Close,Volume,Adj Close
   */
  @RequestMapping("/yahoo/historic/direct/{ticker:.+}")
  public ResponseEntity<String> yahooFinanceHistoricDirect(@PathVariable @NotNull String ticker) {
    logger.info("yahooFinanceHistoricDirect({})", ticker);
    CharSeq results = doTicksYahooFinanceHistoric(kennel.parseTickersString.apply(ticker).head());

    // create the return type required by this mock API...
    HttpHeaders headers = new HttpHeaders();
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

  /*
   * generate a complete response string for a yahoo finance realtime reply
   */
  private CharSeq doTicksYahooFinanceHistoric(final Ticker ticker) {
    logger.info("ticker:{}", ticker.toString());

    // is ticker new?
    List<Ticker> missing = kennel.findMissingTickers.apply(kennel.dogs, List.of(ticker));
    kennel.dogs = kennel.createAndAddNewDogs.apply(kennel.dogs, missing);

    // get the dogs for the requested tickers...
    DomainObjectGenerator myDog = kennel.findMyDog.apply(kennel.dogs, ticker).get();

    logger.info("manager:{}", kennel.dogs.toJavaList());
    logger.info("dogs:{}", myDog.toString());
    logger.info("missing:{}", missing.toJavaList());

    // generate a historic series of TickJson...
    int daysAgo = 100;
    OffsetDateTime startDate = OffsetDateTime.now().minusDays(daysAgo).withHour(0).withMinute(0).withSecond(0);
    List<TickJson> ticks = Stream.range(1, daysAgo + 1)
        .map(x -> myDog.newTick(startDate.plusDays(x)))
        .reverse()
        .toList();

    // format the results specific to this mock API...
    YahooFinanceHistoric yfh = YahooFinanceHistoric.of(ticks);

    // .map(tick -> YahooFinanceHistoric.of(tick))
    // .map(yfr -> yfr.toString())
    // .map(CharSeq::of)
    // .getOrElse(CharSeq.empty())
    // .reduce((x, xs) -> x.append('\n').appendAll(xs));

    CharSeq results = CharSeq.of(yfh.toString());
    logger.info("results:{}", results.toString());
    return results;
  }

  /*
   * generate a complete response string for a yahoo finance realtime reply
   */
  private CharSeq doNextTickForYahooFinanceRealtime(final List<Ticker> tickers) {
    logger.info("tickers:{}", tickers.toString());

    // create dogs for any new tickers...
    List<Ticker> missing = kennel.findMissingTickers.apply(kennel.dogs, tickers);
    kennel.dogs = kennel.createAndAddNewDogs.apply(kennel.dogs, missing);

    // get the dogs for the requested tickers...
    List<DomainObjectGenerator> myDogs = kennel.findMyDogs.apply(kennel.dogs, tickers);

    // require that the list of dogs is complete for all tickers...
    // NOTE an exception my be thrown here
    kennel.assertThatDogsContainTickers.apply(myDogs, tickers);

    logger.info("manager:{}", kennel.dogs.toJavaList());
    logger.info("dogs:{}", myDogs.toJavaList());
    logger.info("missing:{}", missing.toJavaList());

    // .map(dog -> Tuple.of(tickerMetadataProvider.findMetadataForTicker(dog.getTicker()), dog.newTick()))
    // .filter(tuple -> tuple._1.isDefined())

    // format the results specific to this mock API...
    CharSeq results = myDogs
        .map(dog -> dog.newTick())
        .map(tick -> YahooFinanceRealtime.of(tick))
        .map(yfr -> yfr.toString())
        .map(CharSeq::of)
        .reduce((x, xs) -> x.append('\n').appendAll(xs));

    logger.info("results:{}", results.toString());
    return results;
  }

  public DogKennel getKennel() {
    return kennel;
  }

  public void setKennel(DogKennel kennel) {
    this.kennel = kennel;
  }

}

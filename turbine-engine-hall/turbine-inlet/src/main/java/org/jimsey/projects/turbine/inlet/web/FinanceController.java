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

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.jimsey.projects.turbine.inlet.domain.DogKennel;
import org.jimsey.projects.turbine.inlet.domain.TickerMetadataProvider;
import org.jimsey.projects.turbine.inlet.domain.YahooFinanceRealtime;
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

import javaslang.Tuple;
import javaslang.collection.CharSeq;
import javaslang.collection.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private DogKennel kennel;

  @Autowired
  private TickerMetadataProvider tickerMetadataProvider;

  /**
   * http://finance.yahoo.com/d/quotes.csv?f=nxohgav&amp;s=BHP.AX+BLT.L+AAPL
   * http://localhost:48006/finance/yahoo/realtime/ABC
   * 
   * "BHP BLT FPO","ASX",23.20,23.40,23.00,23.31,9714517
   * "BHP BILLITON PLC ORD $0.50","LSE",1225.00,1255.00,1222.00,1232.50,8947122
   * "Apple Inc.","NMS",114.43,114.56,113.51,113.87,13523517
   */

  /** 
   * @param tickersString a '+' separated list of tickers with extension, e.g. ABC.L+DEF.L 
   * @return an attachment containing a CSV string of name,market,open,high,low,close,volume
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
   * @return a CSV string of name,market,open,high,low,close,volume
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

    // format the results specific to this mock API...
    CharSeq results = myDogs
        .map(dog -> Tuple.of(tickerMetadataProvider.findMetadataForTicker(dog.getTicker()), dog.newTick()))
        .filter(tuple -> tuple._1.isDefined())
        .map(tuple -> YahooFinanceRealtime.of(tuple._1.get(), tuple._2))
        .map(yfr -> yfr.toString())
        .map(CharSeq::of)
        .reduce((x, xs) -> x.append('\n').appendAll(xs));

    logger.info("results:{}", results.toString());
    return results;
  }

  public TickerMetadataProvider getSymbolMetadataProvider() {
    return tickerMetadataProvider;
  }

  public void setSymbolMetadataProvider(TickerMetadataProvider tickerMetadataProvider) {
    this.tickerMetadataProvider = tickerMetadataProvider;
  }

  public DogKennel getKennel() {
    return kennel;
  }

  public void setKennel(DogKennel kennel) {
    this.kennel = kennel;
  }

}

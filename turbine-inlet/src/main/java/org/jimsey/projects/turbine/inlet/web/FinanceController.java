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
package org.jimsey.projects.turbine.inlet.web;

import static javaslang.Predicates.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.invoke.MethodHandles;

import javax.validation.constraints.NotNull;

import org.assertj.core.api.AbstractIterableAssert;
import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javaslang.Function2;
import javaslang.collection.CharSeq;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Try;

@Controller
@RequestMapping("/finance")
public class FinanceController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String market = "FTSE100";

  private List<DomainObjectGenerator> dogs = List.empty();

  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<CharSeq>> findMissingSymbols =
    (dogs, symbols) -> symbols.filter(symbol -> !dogs.exists(dog -> symbol.eq(dog.getSymbol())));

  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<DomainObjectGenerator>> createAndAddNewDogs =
    (dogs, symbols) -> dogs.appendAll(symbols.map(symbol -> new RandomDomainObjectGenerator(market, symbol.toString())));
      
  private Function2<List<DomainObjectGenerator>, List<CharSeq>, Object> assertThatDogsContainSymbols =
    (dogs, symbols) -> assertThat(dogs.map(dog -> dog.getSymbol())).containsExactlyInAnyOrder(symbols.toJavaArray(CharSeq.class));

  /**
   * NOTE: javadoc quirk: replace &amp; with just an ampersand to make the link work...
   * http://finance.yahoo.com/d/quotes.csv?f=nxohgav&amp;s=BHP.AX+BLT.L+AAPL
   * http://localhost:48006/finance/yahoo/realtime/ABC
   * 
   * "BHP BLT FPO","ASX",23.20,23.40,23.00,23.31,9714517
   * "BHP BILLITON PLC ORD $0.50","LSE",1225.00,1255.00,1222.00,1232.50,8947122
   * "Apple Inc.","NMS",114.43,114.56,113.51,113.87,13523517
   * 
   * @return
   */
  @RequestMapping("/yahoo/realtime/{symbolsString}")
  public ResponseEntity<String> yahooFinanceRealtime(@PathVariable @NotNull String symbolsString) {
    logger.info("yahooFinanceRealtime({})", symbolsString);
    List<CharSeq> symbols = List.of(CharSeq.of(symbolsString).split("\\+"));
    logger.info("List<CharSeq>:{}", symbols.toString());

    // create dogs for any new symbols...
    List<CharSeq> missing = findMissingSymbols.apply(dogs, symbols);
    dogs = createAndAddNewDogs.apply(dogs, missing);

    // get the dogs for the requested symbols...
    List<DomainObjectGenerator> myDogs = dogs.filter(dog -> symbols.contains(dog.getSymbol()));

    // require that the list of dogs is complete for all symbols...
    // NOTE an exception my be thrown here
    assertThatDogsContainSymbols.apply(myDogs, symbols);
    
    logger.info("manager:{}", dogs.toJavaList());
    logger.info("dogs:{}", myDogs.toJavaList());
    logger.info("missing:{}", missing.toJavaList());

    CharSeq results = dogs
        .map(dog -> dog.newTick())
        .map(tick -> String.format("\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%s",
            tick.getSymbol(), tick.getSymbol(), tick.getOpen(), tick.getHigh(), tick.getLow(), tick.getClose(), tick.getVol()))
        .map(CharSeq::of)
        .reduce((xs, x) -> xs.append('\n').appendAll(x));
    
    logger.info("results:{}", results.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("file", "quotes.csv");
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

}

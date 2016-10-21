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

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.TickJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javaslang.Function2;
import javaslang.collection.CharSeq;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Seq;

@Controller
@RequestMapping("/finance")
public class FinanceController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String market = "FTSE100";

  private List<DomainObjectGenerator> manager = List.empty();

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
    List<CharSeq> missing = symbols.filter(symbol -> !manager.exists(dog -> symbol.eq(dog.getSymbol())));
    manager = manager.appendAll(missing.map(symbol -> new RandomDomainObjectGenerator(market, symbol.toString())));

    // get the dogs for the requested symbols...
    List<DomainObjectGenerator> dogs = manager.filter(dog -> symbols.contains(dog.getSymbol()));

    // require that the list of dogs is complete for all symbols...
    assertThat(dogs.map(dog -> dog.getSymbol())).containsExactlyInAnyOrder(symbols.toJavaArray(CharSeq.class));

    logger.info("manager:{}", manager.toJavaList());
    logger.info("dogs:{}", dogs.toJavaList());
    logger.info("missing:{}", missing.toJavaList());

    List<CharSeq> results = dogs
        .map(dog -> dog.newTick())
        .map(tick -> String.format("\"%s\",\"%s\"", tick.getSymbol(), tick.getSymbol()))
        .map(CharSeq::of);
    logger.info("results:{}", results.toJavaList());

    HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // headers.setContentDispositionFormData("file", "quotes.csv");
    ResponseEntity<String> response = new ResponseEntity<>(symbols.toString(), headers, HttpStatus.OK);
    return response;
  }

  Function2<HashMap<CharSeq, DomainObjectGenerator>, List<CharSeq>, Seq<DomainObjectGenerator>> dogsForSymbols = (manager,
      symbols) -> manager.filter(isIn(symbols)).values();

}

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

// import static javaslang.Predicates.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.inlet.domain.SymbolMetadataProvider;
import org.jimsey.projects.turbine.inlet.domain.YahooFinanceRealtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javaslang.Function2;
import javaslang.Predicates;
import javaslang.Tuple;
import javaslang.collection.CharSeq;
import javaslang.collection.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String market = "FTSE100";

  private List<DomainObjectGenerator> dogs = List.empty();

  @Autowired
  private SymbolMetadataProvider symbolMetadataProvider;
  
  /**
   * given a list of dogs and a list of symbols
   * then will return the symbols that are not found in the given list of dogs
   */
  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<CharSeq>> findMissingSymbols =
    (dogs, symbols) -> symbols.filter(symbol -> !dogs.exists(dog -> symbol.eq(dog.getSymbol())));

  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs with new dogs added for the given symbols
   */
  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<DomainObjectGenerator>> createAndAddNewDogs =
      (dogs, symbols) -> dogs.appendAll(findMissingSymbols.apply(dogs, symbols).map(symbol -> new RandomDomainObjectGenerator(market, symbol.toString())));
            
  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs with new dogs added for the given symbols
   */
  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<DomainObjectGenerator>> FindMissingAndCreateAndAddNewDogs =
      (dogs, symbols) -> dogs.appendAll(symbols.map(symbol -> new RandomDomainObjectGenerator(market, symbol.toString())));
      
  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs for just the given symbols
   */
  private Function2<List<DomainObjectGenerator>, List<CharSeq>, List<DomainObjectGenerator>> findMyDogs =
      (dogs, symbols) -> dogs.filter(dog -> symbols.contains(dog.getSymbol()));
              
  /**
   * given a list of dogs and a list of symbols
   * then will throw an exception if the list of dogs contains more or less dogs than for the given list of symbols
   */
  // TODO this function throws and exception instead of return value, how best to handle? Use a Try?
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
    List<DomainObjectGenerator> myDogs = findMyDogs.apply(dogs,  symbols);

    // require that the list of dogs is complete for all symbols...
    // NOTE an exception my be thrown here
    assertThatDogsContainSymbols.apply(myDogs, symbols);
    
    logger.info("manager:{}", dogs.toJavaList());
    logger.info("dogs:{}", myDogs.toJavaList());
    logger.info("missing:{}", missing.toJavaList());
    
    // format the results specific to this mock API...
    CharSeq results = myDogs
        .map(dog -> dog.newTick())
        .map(tick -> Tuple.of(symbolMetadataProvider.findMetadataForMarketAndSymbol(tick.getMarket(), tick.getSymbol()), tick))
        .filter(tuple -> Objects.nonNull(tuple._1()))
        .map(tuple -> {return new YahooFinanceRealtime(tuple._1, tuple._2);})
        .map(yfr -> yfr.toString())
        .map(CharSeq::of)
        .reduce((xs, x) -> xs.append('\n').appendAll(x));
    
    logger.info("results:{}", results.toString());

    // create the return type required by this mock API...
    HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // headers.setContentDispositionFormData("file", "quotes.csv");
    ResponseEntity<String> response = new ResponseEntity<>(results.toString(), headers, HttpStatus.OK);
    return response;
  }

  public SymbolMetadataProvider getSymbolMetadataProvider() {
    return symbolMetadataProvider;
  }

  public void setSymbolMetadataProvider(SymbolMetadataProvider symbolMetadataProvider) {
    this.symbolMetadataProvider = symbolMetadataProvider;
  }

}

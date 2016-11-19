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

import static org.assertj.core.api.Assertions.*;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.springframework.stereotype.Component;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.collection.CharSeq;
import javaslang.collection.List;
import javaslang.collection.Stream;

@Component
public class DogKennel {

  // TODO make market independent...
  // private final String market = "FTSE100";

  /**
   * public, immutable list of dogs
   */
  public List<DomainObjectGenerator> dogs = List.empty();  
  
  /**
   * given a '+' separated string of tickers (eg. ABC.L), will return a list of Tickers for the
   */
  public Function1<String, List<Ticker>> parseTickersString = tickers -> Stream.of(CharSeq.of(tickers).split("\\+")).map(t -> Ticker.of(t)).toList();

  /**
   * given a list of dogs and a list of symbols
   * then will return the symbols that are not found in the given list of dogs
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<Ticker>> findMissingTickers = (dogs, tickers) -> tickers
      .filter(ticker -> !dogs.exists(dog -> ticker.equals(dog.getTicker())));

  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs with new dogs added for the given symbols
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> createAndAddNewDogs = (dogs,
      tickers) -> dogs.appendAll(
          findMissingTickers.apply(dogs, tickers).map(ticker -> new RandomDomainObjectGenerator(ticker)));

  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs with new dogs added for the given symbols
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> findMissingAndCreateAndAddNewDogs = (
      dogs, tickers) -> dogs.appendAll(tickers.map(ticker -> new RandomDomainObjectGenerator(ticker)));

  /**
   * given a list of dogs and a list of symbols
   * then will return a list of dogs for just the given symbols
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> findMyDogs = (dogs, tickers) -> dogs
      .filter(dog -> tickers.contains(dog.getTicker()));

  /**
   * given a list of dogs and a list of symbols
   * then will throw an exception if the list of dogs contains more or less dogs than for the given list of symbols
   */
  // TODO this function throws an exception instead of return value, how best to handle? Use a Try?
  public Function2<List<DomainObjectGenerator>, List<Ticker>, Object> assertThatDogsContainTickers = (dogs,
      tickers) -> assertThat(dogs.map(dog -> dog.getTicker())).containsExactlyInAnyOrder(tickers.toJavaArray(Ticker.class));

}

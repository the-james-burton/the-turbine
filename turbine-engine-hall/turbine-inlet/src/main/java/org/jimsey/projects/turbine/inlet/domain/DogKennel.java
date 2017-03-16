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

import static java.lang.String.*;
import static org.assertj.core.api.Assertions.*;

import org.jimsey.projects.turbine.fuel.domain.DomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.RandomDomainObjectGenerator;
import org.jimsey.projects.turbine.fuel.domain.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.collection.CharSeq;
import javaslang.collection.List;
import javaslang.collection.Stream;
import javaslang.control.Option;

@Component
public class DogKennel {

  @Autowired
  private TickerMetadata metadata;

  /**
   * public, immutable list of dogs
   */
  public List<DomainObjectGenerator> dogs = List.empty();

  /**
   * given a '+' separated string of tickers (eg. ABC.L), will return a list of enriched Tickers
   */
  public Function1<String, List<Ticker>> parseTickersString = tickers -> Stream.of(CharSeq.of(tickers)
      .split("\\+"))
      .map(t -> CharSeq.of(t))
      .map(s -> metadata.findTickerBySymbol.apply(s)
          .getOrElseThrow(() -> new RuntimeException(format("unable to find ticker:%s in metadata", s))))
      .toList();

  /**
   * given a list of dogs and a list of tickers
   * then will return the tickers that are not found in the given list of dogs
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<Ticker>> findMissingTickers = (dogs, tickers) -> tickers
      .filter(ticker -> !dogs.exists(dog -> ticker.equals(dog.getTicker())));

  /**
   * given a list of dogs and a list of tickers
   * then will return a list of dogs with new dogs added for the given tickers
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> createAndAddNewDogs = (dogs,
      tickers) -> dogs.appendAll(
          findMissingTickers.apply(dogs, tickers).map(ticker -> new RandomDomainObjectGenerator(ticker)));

  /**
   * given a list of dogs and a list of tickers
   * then will return a list of dogs with new dogs added for the given tickers
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> findMissingAndCreateAndAddNewDogs = (
      dogs, tickers) -> dogs.appendAll(tickers.map(ticker -> new RandomDomainObjectGenerator(ticker)));

  /**
   * given a list of dogs and a list of tickers
   * then will return a list of dogs for just the given tickers
   */
  public Function2<List<DomainObjectGenerator>, List<Ticker>, List<DomainObjectGenerator>> findMyDogs = (dogs, tickers) -> dogs
      .filter(dog -> tickers.contains(dog.getTicker()));

  /**
   * given a list of dogs and a ticker
   * then will return a dog for just the given ticker
   */
  public Function2<List<DomainObjectGenerator>, Ticker, Option<DomainObjectGenerator>> findMyDog = (dogs, ticker) -> dogs
      .find(dog -> dog.getTicker().equals(ticker));

  /**
   * given a list of dogs and a list of tickers
   * then will throw an exception if the list of dogs contains more or less dogs than for the given list of tickers
   */
  // TODO this function throws an exception instead of return value, how best to handle? Use a Try?
  public Function2<List<DomainObjectGenerator>, List<Ticker>, Object> assertThatDogsContainTickers = (dogs,
      tickers) -> assertThat(dogs.map(dog -> dog.getTicker())).containsExactlyInAnyOrder(tickers.toJavaArray(Ticker.class));

  public TickerMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(TickerMetadata metadata) {
    this.metadata = metadata;
  }

}

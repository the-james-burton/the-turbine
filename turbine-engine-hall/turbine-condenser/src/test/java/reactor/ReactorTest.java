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
package reactor;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Operators;

public class ReactorTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Test
  public void manyGroups() {

    Map<Integer, AssertSubscriber<Integer>> subscribers = new HashMap<>();

    AssertSubscriber<Integer> errors = AssertSubscriber.create();
    errors.onSubscribe(Operators.emptySubscription());

    Flux.range(0, 10)
        .groupBy(v -> v % 2)
        .doOnNext(v -> subscribers.computeIfAbsent(v.key(), k -> AssertSubscriber.<Integer> create()))
        .doOnNext(v -> logger.info("outerOnNext:{}", v))
        .subscribe(new Subscriber<GroupedFlux<Integer, Integer>>() {
          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }

          @Override
          public void onNext(GroupedFlux<Integer, Integer> t) {
            t.doOnNext(x -> logger.info(" inner onNext{}:{}", x, subscribers.get(t.key())))
                .subscribe(subscribers.get(t.key()));
          }

          @Override
          public void onError(Throwable t) {
            errors.onError(t);
          }

          @Override
          public void onComplete() {
            errors.onComplete();
          }
        });

    subscribers.values().stream()
        .forEach(s -> s.await(Duration.ofSeconds(5)));
    errors.await(Duration.ofSeconds(5));

    subscribers.values().stream()
        .map(v -> v.values)
        .flatMap(xs -> xs.stream())
        .forEach(x -> System.out.println(x));

    subscribers.values().stream()
        .forEach(s -> {
          s.assertValueCount(5)
              .assertNoError()
              .assertComplete();
        });

    errors.assertNoValues()
        .assertNoError()
        .assertComplete();

  }

}

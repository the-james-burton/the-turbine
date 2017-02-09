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
package org.jimsey.projects.turbine.condenser.reactor;

import java.lang.invoke.MethodHandles;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseSubscriber<T> implements Subscriber<T>, Subscription {

  static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  final String name;

  Subscription subscription;

  public BaseSubscriber(String name) {
    this.name = name;
  }

  @Override
  public void request(long n) {
    subscription.request(n);
  }

  @Override
  public void cancel() {
    subscription.cancel();
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
    logger.info("{} onSubscribe:{}", name, subscription.toString());
  }

  @Override
  public void onNext(T t) {
    this.subscription.request(1);
  }

  @Override
  public void onError(Throwable t) {
    logger.error("{} onError:{}", name, t.getMessage());
  }

  @Override
  public void onComplete() {
    logger.info("{} onComplete", name);
  }

}

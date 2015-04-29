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
package org.jimsey.projects.turbine.spring.domain;

public class Quote extends Entity {

  private static final long serialVersionUID = 1L;

  private Instrument mInstrument;

  private Trader mTrader;

  private Double mBid;

  private Double mOffer;

  // -------------------------------------------
  public Instrument getInstrument() {
    return mInstrument;
  }

  public void setInstrument(Instrument instrument) {
    mInstrument = instrument;
  }

  public Trader getTrader() {
    return mTrader;
  }

  public void setTrader(Trader trader) {
    mTrader = trader;
  }

  public Double getBid() {
    return mBid;
  }

  public void setBid(Double bid) {
    mBid = bid;
  }

  public Double getOffer() {
    return mOffer;
  }

  public void setOffer(Double offer) {
    mOffer = offer;
  }

}

package org.jimsey.projects.turbine.spring.domain;

public class Trade extends Entity {

  private Quote mQuote;

  private Trader mSeller;

  private Trader mBuyer;

  private Long mSize;
}

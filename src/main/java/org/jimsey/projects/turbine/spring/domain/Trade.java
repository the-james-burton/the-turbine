package org.jimsey.projects.turbine.spring.domain;

public class Trade extends Entity {
	
	private Quote quote;
	
	private Trader seller;
	
	private Trader buyer;
	
	private Long size;
}

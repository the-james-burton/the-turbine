package org.jimsey.projects.turbine.spring.components;

import org.springframework.stereotype.Component;

@Component
public class Ping {

	public long ping() {
		return System.nanoTime();
	}

}

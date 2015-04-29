package org.jimsey.projects.turbine.spring.domain;

public abstract class Entity {

	private Long mId;

	// -------------------------------
	public Long getId() {
		return mId;
	}

	public void setId(Long id) {
		this.mId = id;
	}
	
}

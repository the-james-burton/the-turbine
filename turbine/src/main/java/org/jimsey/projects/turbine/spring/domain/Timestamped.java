package org.jimsey.projects.turbine.spring.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Timestamped implements Serializable {

  private static final long serialVersionUID = 1L;

  private final LocalDateTime timestamp;

  public Timestamped() {
    this.timestamp = LocalDateTime.now();
  }
  
  @JsonCreator
  public Timestamped(@JsonProperty("timestamp") String timestamp) {
    this.timestamp = LocalDateTime.parse(timestamp);
  }
 
  // ------------------------------
  @JsonIgnore
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @JsonProperty("timestamp")
  public String getTimestampAsString() {
    return timestamp.toString();
  }

}

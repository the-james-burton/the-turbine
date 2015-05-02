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

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Entity implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private static ObjectMapper json = new ObjectMapper();

  private final Long id;
  
  private LocalDateTime timestamp = LocalDateTime.now();
  
  public Entity(Long id) {
    this.id = id;
    this.timestamp = LocalDateTime.now();
  }
  
  @Override
  public String toString() {
    String result = null;
    try {
      result = String.format("{\"%s\":%s}",
          this.getClass().getSimpleName(),
          json.writeValueAsString(this)
          );
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
    // return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    // return String.format("{\"%s\":%s}",
    //     this.getClass().getSimpleName(),
    //     ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE));
  }

  // -------------------------------
  public Long getId() {
    return id;
  }

  //public void setId(Long id) {
  //  this.id = id;
  //}

  @JsonIgnore
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @JsonIgnore
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty("timestamp")
  public String getTimestampAsString() {
    return timestamp.toString();
  }
  
  @JsonProperty("timestamp")
  public void setTimestampFromString(String timestamp) {
    this.timestamp = LocalDateTime.parse(timestamp);
  }
  
}

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
package org.jimsey.projects.turbine.condenser.domain.indicators;

import static java.lang.String.*;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import io.vavr.control.Try;

/**
 * Simple, mutable POJO to represent an technical indicator and its configuration
 * @author the-james-burton
 */
@JsonInclude(Include.NON_NULL)
public class IndicatorInstance implements Comparable<IndicatorInstance>, Serializable {

  private static final long serialVersionUID = 1L;

  private static final ObjectMapper json = new ObjectMapper();

  private String classname;

  private Integer timeframe1;

  private Integer timeframe2;

  private Integer timeframe3;

  private IndicatorClientDefinition clientIndicator = new IndicatorClientDefinition();

  public IndicatorInstance() {
  }

  public IndicatorInstance(String classname, Integer timeframe1, Integer timeframe2, Integer timeframe3, boolean overlay) {
    super();
    this.classname = classname;
    this.timeframe1 = timeframe1;
    this.timeframe2 = timeframe2;
    this.timeframe3 = timeframe3;
    getClientIndicator().setName(generateName());
    getClientIndicator().setOverlay(overlay);
  }

  // ----------------------------getTimeframe3
  private static final Comparator<IndicatorInstance> comparator = Comparator
      .comparing((IndicatorInstance t) -> t.classname)
      .thenComparing(t -> t.timeframe1)
      .thenComparing(t -> t.timeframe2)
      .thenComparing(t -> t.timeframe3);

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof IndicatorInstance)) {
      return false;
    }
    IndicatorInstance that = (IndicatorInstance) key;
    return Objects.equals(this.classname, that.classname)
        && Objects.equals(this.timeframe1, that.timeframe1)
        && Objects.equals(this.timeframe2, that.timeframe2)
        && Objects.equals(this.timeframe3, that.timeframe3);
  }

  @JsonIgnore
  public String getName() {
    return getClientIndicator().getName();
  }

  @JsonIgnore
  private String generateName() {
    return generateName(StringUtils.substringAfterLast(classname, "."));
  }

  @JsonIgnore
  public String generateName(String name) {
    Joiner joiner = Joiner.on("_").skipNulls();
    return joiner.join(name, timeframe1, timeframe2, timeframe3);
  }

  @Override
  public int compareTo(IndicatorInstance that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classname, timeframe1, timeframe2, timeframe3);
  }

  @Override
  public String toString() {
    return Try.of(() -> json.writeValueAsString(this))
        .getOrElseThrow(e -> new RuntimeException(format("unable to write [%s,%s,%s,%s] as JSON",
            classname, timeframe1, timeframe2, timeframe3)));
  }

  public Integer getTimeframe1() {
    return timeframe1;
  }

  public void setTimeframe1(Integer timeframe1) {
    this.timeframe1 = timeframe1;
    getClientIndicator().setName(generateName());
  }

  public Integer getTimeframe2() {
    return timeframe2;
  }

  public void setTimeframe2(Integer timeframe2) {
    this.timeframe2 = timeframe2;
    getClientIndicator().setName(generateName());
  }

  public Integer getTimeframe3() {
    return timeframe3;
  }

  public void setTimeframe3(Integer timeframe3) {
    this.timeframe3 = timeframe3;
    getClientIndicator().setName(generateName());
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
    getClientIndicator().setName(generateName());
  }

  public boolean isOverlay() {
    return getClientIndicator().isOverlay();
  }

  public void setOverlay(boolean overlay) {
    getClientIndicator().setOverlay(overlay);
  }

  public IndicatorClientDefinition getClientIndicator() {
    return clientIndicator;
  }

}

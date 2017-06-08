package org.jimsey.projects.turbine.condenser.domain.indicators;

import static java.lang.String.*;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Try;

/**
 * Simple, mutable POJO to represent an technical indicator and its configuration
 * @author the-james-burton
 */
public class IndicatorInstance implements Comparable<IndicatorInstance>, Serializable {

  private static final long serialVersionUID = 1L;

  private static final ObjectMapper json = new ObjectMapper();

  private String name;

  private Integer timeframe1;

  private Integer timeframe2;

  private Integer timeframe3;

  public IndicatorInstance() {
  }

  public IndicatorInstance(String name, Integer timeframe1, Integer timeframe2, Integer timeframe3) {
    super();
    this.name = name;
    this.timeframe1 = timeframe1;
    this.timeframe2 = timeframe2;
    this.timeframe3 = timeframe3;
  }

  // ----------------------------
  private static final Comparator<IndicatorInstance> comparator = Comparator
      .comparing((IndicatorInstance t) -> t.getName())
      .thenComparing(t -> t.getTimeframe1())
      .thenComparing(t -> t.getTimeframe2())
      .thenComparing(t -> t.getTimeframe3());

  @Override
  public boolean equals(Object key) {
    if (key == null || !(key instanceof IndicatorInstance)) {
      return false;
    }
    IndicatorInstance that = (IndicatorInstance) key;
    return Objects.equals(this.name, that.name)
        && Objects.equals(this.timeframe1, that.timeframe1)
        && Objects.equals(this.timeframe2, that.timeframe2)
        && Objects.equals(this.timeframe3, that.timeframe3);
  }

  @Override
  public int compareTo(IndicatorInstance that) {
    return comparator.compare(this, that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, timeframe1, timeframe2, timeframe3);
  }

  @Override
  public String toString() {
    return Try.of(() -> json.writeValueAsString(this))
        .getOrElseThrow(e -> new RuntimeException(format("unable to write [%s,%s,%s,%s] as JSON",
            name, timeframe1, timeframe2, timeframe3)));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getTimeframe1() {
    return timeframe1;
  }

  public void setTimeframe1(Integer timeframe1) {
    this.timeframe1 = timeframe1;
  }

  public Integer getTimeframe2() {
    return timeframe2;
  }

  public void setTimeframe2(Integer timeframe2) {
    this.timeframe2 = timeframe2;
  }

  public Integer getTimeframe3() {
    return timeframe3;
  }

  public void setTimeframe3(Integer timeframe3) {
    this.timeframe3 = timeframe3;
  }

}

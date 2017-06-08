package org.jimsey.projects.turbine.condenser.domain.indicators;

import java.io.Serializable;
import java.util.List;

/**
 * Simple, mutable POJO to represent a collection of technical indicators and their configuration
 * @author the-james-burton
 */
public class IndicatorInstances implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<IndicatorInstance> indicators;

  public IndicatorInstances() {
  }

  public IndicatorInstances(List<IndicatorInstance> indicators) {
    super();
    this.indicators = indicators;
  }

  public List<IndicatorInstance> getIndicators() {
    return indicators;
  }

  public void setIndicators(List<IndicatorInstance> indicators) {
    this.indicators = indicators;
  }

}

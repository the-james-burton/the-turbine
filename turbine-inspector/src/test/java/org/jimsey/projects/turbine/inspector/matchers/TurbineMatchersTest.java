package org.jimsey.projects.turbine.inspector.matchers;

import static org.hamcrest.CoreMatchers.*;
import static org.jimsey.projects.turbine.inspector.matchers.TurbineMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TurbineMatchersTest {

  @Test
  public void testIsNumeric() {
    assertThat("1", isNumeric());
    assertThat("1.2", isNumeric());
    assertThat("100000.10000", isNumeric());
    assertThat("0.00001", isNumeric());
    
    assertThat("1 a", not(isNumeric()));
    assertThat("one", not(isNumeric()));
    assertThat(".", not(isNumeric()));
    assertThat("", not(isNumeric()));
    assertThat(null, not(isNumeric()));
    
    // will fail if matcher is using NumberUtils.isNumber()
    assertThat("001.2", isNumeric());

  }
  
}

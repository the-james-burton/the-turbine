package org.jimsey.projects.turbine.condenser.domain.indicators;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndicatorInstancesTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static ObjectMapper json = new ObjectMapper();

  private final IndicatorInstance indicator1 = new IndicatorInstance("indicator1", 2, 3, 4);
  private final IndicatorInstance indicator2 = new IndicatorInstance("indicator2", 5, 6, 7);

  private final IndicatorInstances instances = new IndicatorInstances(Arrays.asList(indicator1, indicator2));

  @Test
  public void testJson() throws IOException {
    String text = json.writeValueAsString(instances);
    IndicatorInstances instances2 = json.readValue(text, IndicatorInstances.class);
    String text2 = json.writeValueAsString(instances2);
    logger.info(text2);
    assertThat(text).isEqualTo(text2);
    assertThat(instances.getIndicators()).containsAll(instances2.getIndicators());
  }

  @Ignore
  @Test
  public void testSerializable() throws IOException {
    byte[] bytes = SerializationUtils.serialize(instances);
    IndicatorInstances instances2 = (IndicatorInstances) SerializationUtils.deserialize(bytes);
    assertThat(instances.getIndicators()).containsAll(instances2.getIndicators());
  }
}

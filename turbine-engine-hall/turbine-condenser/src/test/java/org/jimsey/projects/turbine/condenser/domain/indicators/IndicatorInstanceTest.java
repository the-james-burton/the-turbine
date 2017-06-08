package org.jimsey.projects.turbine.condenser.domain.indicators;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndicatorInstanceTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static ObjectMapper json = new ObjectMapper();

  @Test
  public void testJson() throws IOException {
    IndicatorInstance indicator = new IndicatorInstance("testname", 5, 6, 7);
    String text = json.writeValueAsString(indicator);
    IndicatorInstance indicator2 = json.readValue(text, IndicatorInstance.class);
    String text2 = json.writeValueAsString(indicator2);
    logger.info(text2);
    assertThat(text).isEqualTo(text2);
    assertThat(indicator).isEqualTo(indicator);
  }

  @Test
  public void testSerializable() throws IOException {
    IndicatorInstance indicator = new IndicatorInstance("testname", 5, 6, 7);
    byte[] bytes = SerializationUtils.serialize(indicator);
    IndicatorInstance indicator2 = (IndicatorInstance) SerializationUtils.deserialize(bytes);
    assertThat(indicator.getName()).isEqualTo(indicator2.getName());
    assertThat(indicator).isEqualTo(indicator);
  }

}

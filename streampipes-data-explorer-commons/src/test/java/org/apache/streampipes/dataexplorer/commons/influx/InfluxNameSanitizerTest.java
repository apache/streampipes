package org.apache.streampipes.dataexplorer.commons.influx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InfluxNameSanitizerTest {

  @Test
  public void replaceSpecialCharacters(){
    assertEquals("my_name", InfluxNameSanitizer.replaceReservedCharacters("my-name"));
    assertEquals("my_name", InfluxNameSanitizer.replaceReservedCharacters("my name"));
    assertEquals("my_name_", InfluxNameSanitizer.replaceReservedCharacters("my-name "));
  }

  @Test
  public void renameReservedKeywords(){
    assertEquals("from_", InfluxNameSanitizer.renameReservedKeywords("from"));
    assertEquals("FROM_", InfluxNameSanitizer.renameReservedKeywords("FROM"));
  }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.extensions.connectors.plc.adapter.s7.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationParserTest {

  @Test
  public void testGetNodeInformationFromCodePropertyWithComments() {
    var configBlock = """
            // This code block can be used to manually specify the addresses of the PLC registers.
            // The syntax is based on the PLC4X syntax, see [1].
            // Address Pattern:
            // propertyName=%{Memory-Area}{start-address}:{Data-Type}[{array-size}]
            //
            // Sample:
            temperature=%I0.0:INT


            // [1] https://plc4x.apache.org/users/protocols/s7.html
            """;
    var result = new ConfigurationParser().getNodeInformationFromCodeProperty(configBlock);

    assertEquals(1, result.size());
    assertEquals(Set.of("temperature"), result.keySet());
    assertEquals("%I0.0:INT", result.get("temperature"));
  }

  @Test
  public void testGetNodeInformationFromCodePropertyMultipleEntries() {
    var configBlock = """
            v1=%I0.0:INT
            v2=%I0.0:BOOL
            """;
    var result = new ConfigurationParser().getNodeInformationFromCodeProperty(configBlock);

    assertEquals(2, result.size());
    assertEquals(Set.of("v1", "v2"), result.keySet());
    assertEquals("%I0.0:INT", result.get("v1"));
    assertEquals("%I0.0:BOOL", result.get("v2"));
  }

  @Test
  public void testGetStreamPipesDataTypeArray() throws AdapterException {
    var plcType = "INT[100]";
    var result = new ConfigurationParser().getStreamPipesDataType(plcType);

    assertEquals(Datatypes.Integer, result);
  }

  @Test
  public void testGetStreamPipesDataTypeBasic() throws AdapterException {
    var plcType = "INT";
    var result = new ConfigurationParser().getStreamPipesDataType(plcType);

    assertEquals(Datatypes.Integer, result);
  }

  @Test
  public void testGetNodeInformationFromCodePropertyNoEntries() {
    var configBlock = "";
    var result = new ConfigurationParser().getNodeInformationFromCodeProperty(configBlock);

    assertEquals(0, result.size());
  }

  @Test
  public void testIsPLCArray() {
    var result = new ConfigurationParser().isPLCArray("%DB3.DB0:BOOL[100]");
    Assertions.assertTrue(result);
  }

  @Test
  public void testIsNoPLCArray() {
    var result = new ConfigurationParser().isPLCArray("%DB3.DB0:BOOL");
    Assertions.assertFalse(result);
  }
}

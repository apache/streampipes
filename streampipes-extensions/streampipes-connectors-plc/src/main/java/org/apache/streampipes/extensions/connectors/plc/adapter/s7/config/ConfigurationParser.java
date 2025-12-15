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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This is a helper class to parse the user input for the PLC4X S7 adapter
 */
public class ConfigurationParser {

  /**
   * This method takes a string with the PLC configuration and parses the configuration accorting to this pattern:
   * variableName=value
   *
   * @param codePropertyValue code block with the PLC configuration
   * @return returns a list of Maps, with the variable name as the Key and value as value
   */
  public Map<String, String> getNodeInformationFromCodeProperty(String codePropertyValue) {
    var result = new HashMap<String, String>();

    var lines = codePropertyValue.split("\n");

    // pattern to match "variableName=value"
    var pattern = Pattern.compile("(\\w+)=(.+)");

    for (String line : lines) {
      // Remove leading and trailing whitespace
      line = line.trim();

      // Skip comments
      if (line.startsWith("//")) {
        continue;
      }

      // Check if macher matches and add to results list
      var matcher = pattern.matcher(line);
      if (matcher.find()) {
        result.put(matcher.group(1), matcher.group(2));
      }
    }

    return result;
  }


  /**
   * Transforms PLC4X data types to datatypes supported in StreamPipes
   *
   * @param plcType String
   * @return Datatypes
   */
  public Datatypes getStreamPipesDataType(String plcType) throws AdapterException {
    var type = extractType(plcType);

    type = removeArrayInformation(type);

    if (isStringWithLengthLimit(type)) {
      return Datatypes.String;
    }

    return mapTypeToDatatype(type, plcType);
  }

  private String extractType(String plcType) {
    return plcType.substring(plcType.lastIndexOf(":") + 1);
  }

  private String removeArrayInformation(String type) {
    return type.replaceAll("\\[.*?\\]", "");
  }

  private boolean isStringWithLengthLimit(String type) {
    return type.startsWith("STRING(");
  }

  private Datatypes mapTypeToDatatype(String type, String plcType) throws AdapterException {
    return switch (type) {
      case "BOOL" -> Datatypes.Boolean;
      case "BYTE", "REAL" -> Datatypes.Float;
      case "LREAL" -> Datatypes.Double;
      case "INT", "DINT", "UDINT", "UINT", "SINT", "USINT" -> Datatypes.Integer;
      case "LINT", "ULINT" -> Datatypes.Long;
      case "WORD", "LWORD", "TIME_OF_DAY", "DATE", "DATE_AND_TIME", "STRING", "CHAR", "WCHAR" -> Datatypes.String;
      default -> throw new AdapterException("Datatype " + plcType + " is not supported");
    };
  }

  /**
   * Takes the PLC4X address description and validates if it describes an array
   *
   * @param plcType address of the register that should be read
   * @return whether the address describes an array or not
   */
  public boolean isPLCArray(String plcType) {
    return plcType.matches(".*\\[.*\\].*");
  }


}

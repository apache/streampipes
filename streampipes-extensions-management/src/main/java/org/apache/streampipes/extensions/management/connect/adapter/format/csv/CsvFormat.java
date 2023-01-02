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

package org.apache.streampipes.extensions.management.connect.adapter.format.csv;


import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.FormatDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.Labels;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsvFormat implements IFormat {

  public static final String HEADER_NAME = "header";
  public static final String DELIMITER_NAME = "delimiter";

  private String[] keyValues = null;
  private String delimiter;
  private Boolean header;

  public static final String ID = "https://streampipes.org/vocabulary/v1/format/csv";

  public CsvFormat() {

  }

  public CsvFormat(String delimiter, Boolean header) {
    this.delimiter = delimiter;
    this.header = header;
  }

  @Override
  public IFormat getInstance(FormatDescription formatDescription) {
    ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
    String delimiter = extractor.singleValue(DELIMITER_NAME);

    boolean header = extractor.selectedMultiValues(HEADER_NAME).stream()
        .anyMatch(option -> "Header".equals(option));


    return new CsvFormat(delimiter, header);
  }

  @Override
  public void reset() {
    this.keyValues = null;
  }

  @Override
  public Map<String, Object> parse(byte[] object) throws ParseException {
    String[] arr = CsvParser.parseLine(new String(object), delimiter);
    Map<String, Object> map = new HashMap<>();

    if (keyValues == null && !header) {
      keyValues = new String[arr.length];
      for (int i = 0; i < arr.length; i++) {
        keyValues[i] = "key_" + i;
      }
    }

    if (keyValues == null) {
      keyValues = new String[arr.length];
      for (int i = 0; i < arr.length; i++) {
        keyValues[i] = arr[i];
      }

    } else {
      for (int i = 0; i <= arr.length - 1; i++) {

        if (!arr[i].equals("") && StringUtils.isNumeric(arr[i])) {
          // If there is a number format exception for long, transform it to a dauble
          try {
            map.put(keyValues[i], Long.parseLong(arr[i]));
          } catch (NumberFormatException e) {
            // If not a double use string as fallback type
            map.put(keyValues[i], Double.parseDouble(arr[i]));
          }
        } else if ("true".equals(arr[i].toLowerCase()) || "false".equals(arr[i].toLowerCase())) {
          map.put(keyValues[i], Boolean.parseBoolean(arr[i]));
        } else {

          try {
            Double doubleValue = Double.parseDouble(arr[i]);
            map.put(keyValues[i], doubleValue);

          } catch (NumberFormatException e) {
            // If not a double use string as fallback type
            map.put(keyValues[i], arr[i]);
          }

        }
      }

    }

    if (map.keySet().size() == 0) {
      return null;
    } else {
      return map;
    }
  }

  @Override
  public FormatDescription declareModel() {

    return FormatDescriptionBuilder.create(ID, "Csv", "Can be used to read CSV")
        .requiredTextParameter(Labels.from("delimiter", "Delimiter",
            "The delimiter for json. Mostly either , or ;"))
        .requiredMultiValueSelection(Labels.from("header", "Header",
                "Does the CSV file include a header or not"),
            Arrays.asList(new Option("Header", "Header")))
        .build();

  }


  @Override
  public String getId() {
    return ID;
  }
}

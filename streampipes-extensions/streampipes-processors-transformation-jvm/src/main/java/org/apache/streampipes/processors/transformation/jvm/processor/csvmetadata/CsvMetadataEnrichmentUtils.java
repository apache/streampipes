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
package org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;

import java.io.IOException;
import java.io.StringReader;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class CsvMetadataEnrichmentUtils {

  public static CSVParser getCsvParser(String fileContents) throws IOException {
    return new CSVParser(new StringReader(fileContents),
        CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader());
  }

  public static EventProperty getGuessedEventProperty(String columnName, CSVRecord firstRecord) {
    return PrimitivePropertyBuilder
        .create(getGuessDatatype(columnName, firstRecord), columnName)
        .build();
  }

  public static Datatypes getGuessDatatype(String columnName, CSVRecord firstRecord) {
    String recordValue = firstRecord.get(columnName);
    if (isNumeric(recordValue)) {
      return Datatypes.Float;
    } else if (isBoolean(recordValue)) {
      return Datatypes.Boolean;
    } else {
      return Datatypes.String;
    }
  }

  private static boolean isBoolean(String recordValue) {
    return BooleanUtils.toBooleanObject(recordValue) != null;
  }
}

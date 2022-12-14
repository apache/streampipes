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


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.parser.BooleanParser;
import org.apache.streampipes.commons.parser.FloatParser;
import org.apache.streampipes.commons.parser.IntegerParser;
import org.apache.streampipes.commons.parser.PrimitiveTypeParser;
import org.apache.streampipes.commons.parser.StringParser;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvMetadataEnrichment implements EventProcessor<CsvMetadataEnrichmentParameters> {

  private static final Logger LOG = LoggerFactory.getLogger(CsvMetadataEnrichment.class);

  private String mappingFieldSelector;
  private String matchingColumn;
  private List<Tuple2<String, PrimitiveTypeParser>> columnsToAppend;
  private Map<String, CSVRecord> columnMap;

  @Override
  public void onInvocation(CsvMetadataEnrichmentParameters parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.mappingFieldSelector = parameters.getMappingFieldSelector();
    this.matchingColumn = parameters.getLookupField();
    try {
      makeColumnMap(parameters.getFileContents());
    } catch (IOException e) {
      throw new SpRuntimeException(e);
    }
    if (this.columnMap.size() > 0) {
      this.columnsToAppend = parameters
          .getFieldsToAppend()
          .stream()
          .map(c -> makeParser(c, this.columnMap.entrySet().stream().findFirst().get().getValue()))
          .collect(Collectors.toList());
    } else {
      LOG.warn("Could not find any rows, does the CSV file contain data?");
      this.columnsToAppend = new ArrayList<>();
    }
  }

  private Tuple2<String, PrimitiveTypeParser> makeParser(String columnName,
                                                         CSVRecord record) {
    Datatypes columnDatatype = CsvMetadataEnrichmentUtils.getGuessDatatype(columnName, record);
    if (columnDatatype.equals(Datatypes.Float)) {
      return new Tuple2<>(columnName, new FloatParser());
    } else if (columnDatatype.equals(Datatypes.Integer)) {
      return new Tuple2<>(columnName, new IntegerParser());
    } else if (columnDatatype.equals(Datatypes.Boolean)) {
      return new Tuple2<>(columnName, new BooleanParser());
    } else {
      return new Tuple2<>(columnName, new StringParser());
    }
  }

  private void makeColumnMap(String fileContents) throws IOException {
    CSVParser parser = CsvMetadataEnrichmentUtils.getCsvParser(fileContents);
    List<CSVRecord> records = parser.getRecords();
    this.columnMap = new HashMap<>();
    for (CSVRecord record : records) {
      this.columnMap.put(record.get(matchingColumn), record);
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String lookupValue =
        event.getFieldBySelector(mappingFieldSelector).getAsPrimitive().getAsString();
    CSVRecord record = this.columnMap.get(lookupValue);
    for (Tuple2<String, PrimitiveTypeParser> columnToAppend : columnsToAppend) {
      event.addField(columnToAppend.k, getRecordValueOrDefault(record, columnToAppend));
    }
    collector.collect(event);
  }

  private Object getRecordValueOrDefault(CSVRecord record, Tuple2<String,
      PrimitiveTypeParser> columnToAppend) {
    if (record != null) {
      return columnToAppend.v.parse(record.get(columnToAppend.k));
    } else {
      return columnToAppend.v.parse("0");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.columnMap = new HashMap<>();
  }
}

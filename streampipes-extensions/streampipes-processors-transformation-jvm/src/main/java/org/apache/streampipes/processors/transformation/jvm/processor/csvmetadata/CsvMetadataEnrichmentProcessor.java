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


import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.parser.BooleanParser;
import org.apache.streampipes.commons.parser.FloatParser;
import org.apache.streampipes.commons.parser.IntegerParser;
import org.apache.streampipes.commons.parser.PrimitiveTypeParser;
import org.apache.streampipes.commons.parser.StringParser;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentUtils.getCsvParser;

public class CsvMetadataEnrichmentProcessor
    extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOptions,
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final Logger LOG = LoggerFactory.getLogger(CsvMetadataEnrichmentProcessor.class);

  private static final String MAPPING_FIELD_KEY = "mapping-field";
  private static final String CSV_FILE_KEY = "csv-file";
  private static final String FIELDS_TO_APPEND_KEY = "fields-to-append";
  private static final String FIELD_TO_MATCH = "field-to-match";

  private String mappingFieldSelector;
  private String matchingColumn;
  private List<Tuple2<String, PrimitiveTypeParser>> columnsToAppend;
  private Map<String, CSVRecord> columnMap;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm"
            + ".csvmetadata")
        .category(DataProcessorType.ENRICH)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.anyProperty(),
                Labels.withId(MAPPING_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .requiredFile(Labels.withId(CSV_FILE_KEY), Filetypes.CSV)
        .requiredSingleValueSelectionFromContainer(Labels.withId(FIELD_TO_MATCH),
            Arrays.asList(MAPPING_FIELD_KEY, CSV_FILE_KEY))
        .requiredMultiValueSelectionFromContainer(Labels.withId(FIELDS_TO_APPEND_KEY),
            Arrays.asList(MAPPING_FIELD_KEY, CSV_FILE_KEY, FIELD_TO_MATCH))
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public List<Option> resolveOptions(String requestId,
                                     IStaticPropertyExtractor parameterExtractor) {
    try {
      String fileContents = getFileContents(parameterExtractor);
      if (requestId.equals(FIELDS_TO_APPEND_KEY)) {
        String matchColumn = parameterExtractor.selectedSingleValue(FIELD_TO_MATCH, String.class);
        return getOptionsFromColumnNames(fileContents, Collections.singletonList(matchColumn));
      } else {
        return getOptionsFromColumnNames(fileContents, Collections.emptyList());
      }
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor parameterExtractor)
      throws SpRuntimeException {
    List<EventProperty> properties = processingElement
        .getInputStreams()
        .get(0)
        .getEventSchema()
        .getEventProperties();

    List<String> columnsToInclude = parameterExtractor.selectedMultiValues(FIELDS_TO_APPEND_KEY,
        String.class);
    try {
      String fileContents = getFileContents(parameterExtractor);
      properties.addAll(getAppendProperties(fileContents, columnsToInclude));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new EventSchema(properties);
  }

  private List<EventProperty> getAppendProperties(String fileContents,
                                                  List<String> columnsToInclude) throws IOException {
    CSVParser parser = getCsvParser(fileContents);
    List<EventProperty> propertiesToAppend = new ArrayList<>();
    List<CSVRecord> records = parser.getRecords();
    if (records.size() > 0) {
      CSVRecord firstRecord = records.get(0);
      for (String column : columnsToInclude) {
        propertiesToAppend.add(makeEventProperty(column, firstRecord));
      }
    }
    return propertiesToAppend;
  }

  private EventProperty makeEventProperty(String column, CSVRecord firstRecord) {
    return CsvMetadataEnrichmentUtils.getGuessedEventProperty(column, firstRecord);
  }

  private List<Option> getOptionsFromColumnNames(String fileContents,
                                                 List<String> columnsToIgnore) throws IOException {
    return getColumnNames(fileContents, columnsToIgnore)
        .stream()
        .map(Option::new)
        .collect(Collectors.toList());
  }

  private List<String> getColumnNames(String fileContents, List<String> columnsToIgnore) throws IOException {
    CSVParser parser = getCsvParser(fileContents);
    return parser
        .getHeaderMap()
        .keySet()
        .stream()
        .filter(key -> columnsToIgnore.stream().noneMatch(c -> c.equals(key)))
        .collect(Collectors.toList());
  }

  private String getFileContents(IParameterExtractor<?> extractor) {
    String filename = extractor.selectedFilename(CSV_FILE_KEY);
    return getStreamPipesClientInstance().fileApi().getFileContentAsString(filename);
  }

  private StreamPipesClient getStreamPipesClientInstance() {
    return new StreamPipesClientResolver().makeStreamPipesClientInstance();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    mappingFieldSelector = extractor.mappingPropertyValue(MAPPING_FIELD_KEY);
    List<String> fieldsToAppend = extractor.selectedMultiValues(FIELDS_TO_APPEND_KEY, String.class);
    matchingColumn = extractor.selectedSingleValue(FIELD_TO_MATCH, String.class);
    String fileContents = getFileContents(extractor);

    try {
      makeColumnMap(fileContents);
    } catch (IOException e) {
      throw new SpRuntimeException(e);
    }
    if (this.columnMap.size() > 0) {
      this.columnsToAppend = fieldsToAppend
          .stream()
          .map(c -> makeParser(c, this.columnMap.entrySet().stream().findFirst().get().getValue()))
          .collect(Collectors.toList());
    } else {
      LOG.warn("Could not find any rows, does the CSV file contain data?");
      this.columnsToAppend = new ArrayList<>();
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

  @Override
  public void onDetach() throws SpRuntimeException {
    this.columnMap = new HashMap<>();
  }

  private Object getRecordValueOrDefault(CSVRecord record, Tuple2<String,
      PrimitiveTypeParser> columnToAppend) {
    if (record != null) {
      return columnToAppend.v.parse(record.get(columnToAppend.k));
    } else {
      return columnToAppend.v.parse("0");
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
}

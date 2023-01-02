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
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentUtils.getCsvParser;

public class CsvMetadataEnrichmentController
    extends StandaloneEventProcessingDeclarer<CsvMetadataEnrichmentParameters>
    implements ResolvesContainerProvidedOptions,
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String MAPPING_FIELD_KEY = "mapping-field";
  private static final String CSV_FILE_KEY = "csv-file";
  private static final String FIELDS_TO_APPEND_KEY = "fields-to-append";
  private static final String FIELD_TO_MATCH = "field-to-match";

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
  public ConfiguredEventProcessor<CsvMetadataEnrichmentParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {
    String mappingFieldSelector = extractor.mappingPropertyValue(MAPPING_FIELD_KEY);
    List<String> fieldsToAppend = extractor.selectedMultiValues(FIELDS_TO_APPEND_KEY, String.class);
    String lookupField = extractor.selectedSingleValue(FIELD_TO_MATCH, String.class);
    String fileContents = getFileContents(extractor);

    CsvMetadataEnrichmentParameters params = new CsvMetadataEnrichmentParameters(graph,
        mappingFieldSelector,
        fieldsToAppend,
        lookupField,
        fileContents);

    return new ConfiguredEventProcessor<>(params, CsvMetadataEnrichment::new);
  }

  @Override
  public List<Option> resolveOptions(String requestId,
                                     StaticPropertyExtractor parameterExtractor) {
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

  private String getFileContents(AbstractParameterExtractor<?> extractor) {
    String filename = extractor.selectedFilename(CSV_FILE_KEY);
    return getStreamPipesClientInstance().fileApi().getFileContentAsString(filename);
  }

  private StreamPipesClient getStreamPipesClientInstance() {
    return new StreamPipesClientResolver().makeStreamPipesClientInstance();
  }
}

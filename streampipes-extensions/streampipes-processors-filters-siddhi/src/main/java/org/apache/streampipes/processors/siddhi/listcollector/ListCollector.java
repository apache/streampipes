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
package org.apache.streampipes.processors.siddhi.listcollector;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;

import java.util.Collections;
import java.util.List;

public class ListCollector extends StreamPipesSiddhiProcessor
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String LIST_KEY = "list-key";
  private static final String WINDOW_SIZE = "window-size";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.listcollector")
        .withLocales(Locales.EN)
        .category(DataProcessorType.TRANSFORM)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId
                (LIST_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredIntegerParameter(Labels.withId(WINDOW_SIZE))
        .outputStrategy(OutputStrategies.customTransformation())
        .build();

  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor) throws SpRuntimeException {
    String propertySelector = extractor.mappingPropertyValue(LIST_KEY);
    EventSchema inputSchema = processingElement.getInputStreams().get(0).getEventSchema();

    List<EventProperty> selectedProperty = extractor
        .getInputStreamEventPropertySubset(Collections.singletonList(propertySelector));

    if (selectedProperty.size() > 0) {
      EventPropertyPrimitive prop = (EventPropertyPrimitive) selectedProperty.get(0);
      String newPropertyName = prop.getRuntimeName() + "_list";
      String newDatatype = prop.getRuntimeType();
      String newDomainProperty = prop.getDomainProperties().get(0).toString();

      EventPropertyList ep = EpProperties.listEp(Labels.from("list", "", ""),
          newPropertyName,
          Datatypes.fromDatatypeString(newDatatype),
          newDomainProperty);

      inputSchema.addEventProperty(ep);
    }

    return inputSchema;
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {
    Integer batchWindowSize = siddhiParams.getParams().extractor().singleValueParameter(WINDOW_SIZE, Integer.class);
    String propertySelector = siddhiParams.getParams().extractor().mappingPropertyValue(LIST_KEY);

    FromClause fromClause = FromClause.create();
    fromClause.add(
        Expressions.stream(siddhiParams.getInputStreamNames().get(0), Expressions.batchWindow(batchWindowSize)));

    SelectClause selectClause = SelectClause.create();
    siddhiParams
        .getEventTypeInfo()
        .forEach((key, value) -> value
            .forEach(field -> selectClause.addProperty(
                Expressions.property(SiddhiStreamSelector.FIRST_INPUT_STREAM, field.getFieldName()))));

    selectClause.addProperty(
        Expressions.as(Expressions.collectList(Expressions.property(propertySelector)), propertySelector + "_list"));

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);
    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder
            .create(fromClause, insertIntoClause)
            .withSelectClause(selectClause)
            .build())
        .build();
  }
}

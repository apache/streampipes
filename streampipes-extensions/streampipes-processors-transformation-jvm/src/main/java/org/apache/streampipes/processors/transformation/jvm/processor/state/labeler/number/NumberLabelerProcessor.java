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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.LabelerUtils;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model.Statement;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model.StatementUtils;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.COMPARATOR_ID;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.LABEL_COLLECTION_ID;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.LABEL_NAME;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.LABEL_STRING_ID;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.NUMBER_VALUE_ID;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.getComparators;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.getLabelName;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.getLabelStrings;
import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.getNumberValues;

public class NumberLabelerProcessor extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String SENSOR_VALUE_ID = "sensorValueId";

  private String sensorListValueProperty;
  private String labelName;
  private List<Statement> statements;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number")
        .category(DataProcessorType.ENRICH)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.numberReq(),
                Labels.withId(SENSOR_VALUE_ID),
                PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.withId(LABEL_NAME))
        .requiredCollection(
            Labels.withId(LABEL_COLLECTION_ID),
            StaticProperties.group(Labels.from("group", "Group", ""), false,
                StaticProperties.singleValueSelection(Labels.withId(COMPARATOR_ID),
                    Options.from("<", "<=", ">", ">=", "==", "*")),
                StaticProperties.doubleFreeTextProperty(Labels.withId(NUMBER_VALUE_ID)),
                StaticProperties.stringFreeTextProperty(Labels.withId(LABEL_STRING_ID))
            )
        )

        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor parameterExtractor)
      throws SpRuntimeException {
    String labelName = getLabelName(parameterExtractor);

    List<String> labelStrings = getLabelStrings(parameterExtractor);

    return LabelerUtils.resolveOutputStrategy(processingElement, labelName, labelStrings);
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = parameters.extractor();
    sensorListValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_ID);

    labelName = getLabelName(extractor);
    List<Double> numberValues = getNumberValues(extractor);
    List<String> labelStrings = getLabelStrings(extractor);

    List<String> comparators = getComparators(extractor);
    statements = StatementUtils.getStatements(
        numberValues,
        labelStrings,
        comparators);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector collector) throws SpRuntimeException {
    Double value = inputEvent.getFieldBySelector(this.sensorListValueProperty).getAsPrimitive().getAsDouble();

    Event resultEvent = StatementUtils.addLabel(inputEvent, labelName, value, this.statements);

    collector.collect(resultEvent);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}

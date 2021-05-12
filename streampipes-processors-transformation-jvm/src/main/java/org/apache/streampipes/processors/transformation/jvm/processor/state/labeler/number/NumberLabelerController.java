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
import org.apache.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.LabelerUtils;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

import static org.apache.streampipes.processors.transformation.jvm.processor.state.StateUtils.*;

public class NumberLabelerController extends StandaloneEventProcessingDeclarer<NumberLabelerParameters> implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String SENSOR_VALUE_ID = "sensorValueId";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number")
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
  public ConfiguredEventProcessor<NumberLabelerParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String sensorListValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_ID);

    String labelName = getLabelName(extractor);

    List<Double> numberValues = getNumberValues(extractor);

    List<String> labelStrings = getLabelStrings(extractor);

    List<String> comparators = getComparators(extractor);


    NumberLabelerParameters params = new NumberLabelerParameters(graph, sensorListValueProperty, labelName, numberValues, labelStrings, comparators);


    return new ConfiguredEventProcessor<>(params, NumberLabeler::new);
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement, ProcessingElementParameterExtractor parameterExtractor) throws SpRuntimeException {
    String labelName = getLabelName(parameterExtractor);

    List<String> labelStrings = getLabelStrings(parameterExtractor);

    return LabelerUtils.resolveOutputStrategy(processingElement, labelName, labelStrings);
  }
}

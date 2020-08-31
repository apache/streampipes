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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;
import java.util.stream.Collectors;

public class StateBufferLabelerController extends StandaloneEventProcessingDeclarer<StateBufferLabelerParameters> {

  public static final String STATE_FILTER_ID = "stateFilterId";
  public static final String STATE_FIELD_ID = "stateFieldId";
  public static final String OPERATIONS_ID = "operationsId";
  public static final String SENSOR_VALUE_ID = "sensorValueId";
  public static final String LABEL_COLLECTION_ID = "labelCollectionId";
  public static final String COMPARATOR_ID = "comparatorId";
  public static final String NUMBER_VALUE_ID = "numberValueId";
  public static final String LABEL_STRING_ID = "labelStringId";

  public static final String LABEL = "label";

  public static final String MINIMUM = "minimum";
  public static final String MAXIMUM = "maximum";
  public static final String AVERAGE = "average";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.listRequirement(),
                            Labels.withId(SENSOR_VALUE_ID),
                            PropertyScope.NONE)
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.domainPropertyReqList(SPSensor.STATE),
                            Labels.withId(STATE_FIELD_ID),
                            PropertyScope.NONE)
                    .build())
            .requiredTextParameter(Labels.withId(STATE_FILTER_ID))
            .requiredSingleValueSelection(Labels.withId(OPERATIONS_ID),
                    Options.from(MINIMUM, MAXIMUM, AVERAGE))
            .requiredCollection(
                    Labels.withId(LABEL_COLLECTION_ID),
                    StaticProperties.group(Labels.from("group", "Group", ""), false,
                            StaticProperties.singleValueSelection(Labels.withId(COMPARATOR_ID),
                                    Options.from("<", "<=", ">", ">=", "==", "*")),
                            StaticProperties.doubleFreeTextProperty(Labels.withId(NUMBER_VALUE_ID)),
                            StaticProperties.stringFreeTextProperty(Labels.withId(LABEL_STRING_ID))
                    )
            )
            .outputStrategy(OutputStrategies.append(
                    EpProperties.stringEp(Labels.withId(LABEL), LABEL, SPSensor.STATE, PropertyScope.DIMENSION_PROPERTY)
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<StateBufferLabelerParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String sensorListValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_ID);
    String stateProperty = extractor.mappingPropertyValue(STATE_FIELD_ID);
    String stateFilter = extractor.singleValueParameter(STATE_FILTER_ID, String.class);
    String selectedOperation = extractor.selectedSingleValue(OPERATIONS_ID, String.class);

    List<StaticPropertyGroup> groupItems = extractor.collectionMembersAsGroup(LABEL_COLLECTION_ID);
    List<Integer> numberValues = groupItems
            .stream()
            .map(group -> (
                    extractor
                            .extractGroupMember(NUMBER_VALUE_ID, group)
                            .as(FreeTextStaticProperty.class))
                    .getValue())
            .map(Integer::parseInt)
            .collect(Collectors.toList());

    List<String> labelStrings = groupItems
            .stream()
            .map(group -> (extractor
                    .extractGroupMember(LABEL_STRING_ID, group)
                    .as(FreeTextStaticProperty.class))
                    .getValue())
            .collect(Collectors.toList());

    List<String> comparators = groupItems
            .stream()
            .map(group -> (extractor
                    .extractGroupMember(COMPARATOR_ID, group)
                    .as(OneOfStaticProperty.class))
                    .getOptions()
                    .stream()
                    .filter(Option::isSelected).findFirst().get().getName())
            .collect(Collectors.toList());
    StateBufferLabelerParameters params = new StateBufferLabelerParameters(graph, sensorListValueProperty, stateProperty, stateFilter, selectedOperation, numberValues, labelStrings, comparators);

    return new ConfiguredEventProcessor<>(params, StateBufferLabeler::new);
  }
}

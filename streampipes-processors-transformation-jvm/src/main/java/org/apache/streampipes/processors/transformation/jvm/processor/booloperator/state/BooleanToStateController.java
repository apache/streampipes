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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
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

public class BooleanToStateController extends StandaloneEventProcessingDeclarer<BooleanToStateParameters> {

  public static final String COLLECTION_ID = "collection-id";
  public static final String GROUP_ID = "group-id";
  public static final String STATE_NAME_ID = "state-name-id";
  public static final String STATE_MAPPING_ID = "state-mapping-id";
  public static final String BOOLEAN_STATE_FIELD = "boolean_state_field";
  public static final String DEFAULT_STATE_ID = "default-state-id";

  public static final String CURRENT_STATE = "current_state";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithNaryMapping(EpRequirements.booleanReq(), Labels.withId(BOOLEAN_STATE_FIELD), PropertyScope.NONE)
                    .build())
            // TODO UI does not render collections with group
//            .requiredParameterAsCollection(
//                    Labels.withId(COLLECTION_ID),
//                    StaticProperties.group(
//                            Labels.withId(GROUP_ID),
//                            StaticProperties.stringFreeTextProperty(Labels.withId(STATE_NAME_ID)),
//                            StaticProperties.mappingPropertyUnary(Labels.withId(STATE_MAPPING_ID), PropertyScope.NONE)))
            .requiredTextParameter(Labels.withId(DEFAULT_STATE_ID))
            .outputStrategy(OutputStrategies.append(
                    EpProperties.listStringEp(Labels.withId(CURRENT_STATE), CURRENT_STATE, SPSensor.STATE)
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<BooleanToStateParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> stateFields = extractor.mappingPropertyValues(BOOLEAN_STATE_FIELD);
    String defaultState = extractor.singleValueParameter(DEFAULT_STATE_ID, String.class);

    BooleanToStateParameters params = new BooleanToStateParameters(graph, stateFields, defaultState);

    return new ConfiguredEventProcessor<>(params, BooleanToState::new);
  }
}

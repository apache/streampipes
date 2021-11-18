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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class StringToStateController extends StandaloneEventProcessingDeclarer<StringToStateParameters> {

  public static final String STRING_STATE_FIELD = "string_state_field";

  public static final String CURRENT_STATE = "current_state";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithNaryMapping(EpRequirements.stringReq(), Labels.withId(STRING_STATE_FIELD), PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.listStringEp(Labels.withId(CURRENT_STATE), CURRENT_STATE, SPSensor.STATE)
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<StringToStateParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> stateFields = extractor.mappingPropertyValues(STRING_STATE_FIELD);

    StringToStateParameters params = new StringToStateParameters(graph, stateFields);

    return new ConfiguredEventProcessor<>(params, StringToState::new);
  }
}

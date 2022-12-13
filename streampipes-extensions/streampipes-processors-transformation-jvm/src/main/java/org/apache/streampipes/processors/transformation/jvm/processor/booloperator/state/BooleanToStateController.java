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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class BooleanToStateController extends StandaloneEventProcessingDeclarer<BooleanToStateParameters> {

  private static final Logger LOG = LoggerFactory.getLogger(BooleanToStateController.class);

  public static final String BOOLEAN_STATE_FIELD = "boolean_state_field";
  public static final String DEFAULT_STATE_ID = "default-state-id";
  public static final String JSON_CONFIGURATION = "json-configuration";

  public static final String CURRENT_STATE = "current_state";

  private static final String defaultSkeleton = "// Add the configuration for the string mappings here:\n"
      + "{\n"
      + "     \"exampleRuntimeName1\": \"newValue1\",\n"
      + "     \"exampleRuntimeName2\": \"newValue2\"\n"
      + "}";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state")
        .category(DataProcessorType.BOOLEAN_OPERATOR)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithNaryMapping(EpRequirements.booleanReq(), Labels.withId(BOOLEAN_STATE_FIELD),
                PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.withId(DEFAULT_STATE_ID))
        .requiredCodeblock(Labels.withId(JSON_CONFIGURATION), CodeLanguage.Javascript, defaultSkeleton)
        .outputStrategy(OutputStrategies.append(
            EpProperties.stringEp(Labels.withId(CURRENT_STATE), CURRENT_STATE, SPSensor.STATE)
        ))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<BooleanToStateParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {

    List<String> stateFields = extractor.mappingPropertyValues(BOOLEAN_STATE_FIELD);
    String defaultState = extractor.singleValueParameter(DEFAULT_STATE_ID, String.class);
    String jsonConfigurationString = extractor.codeblockValue(JSON_CONFIGURATION);

    try {
      jsonConfigurationString = jsonConfigurationString.replaceAll("(?m)^//.*", "");
      Map<String, String> jsonConfiguration =
          JacksonSerializer.getObjectMapper().readValue(jsonConfigurationString, Map.class);
      BooleanToStateParameters params =
          new BooleanToStateParameters(graph, stateFields, defaultState, jsonConfiguration);

      return new ConfiguredEventProcessor<>(params, BooleanToState::new);
    } catch (JsonProcessingException e) {
      LOG.info("Error when parsing the json configuration: " + jsonConfigurationString);
      throw new SpRuntimeException("The following mapping configuration is not valid: " + jsonConfigurationString);
    }
  }

}

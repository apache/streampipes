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

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
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

public class NumberLabelerController extends StandaloneEventProcessingDeclarer<NumberLabelerParameters> {

  public static final String SENSOR_VALUE_ID = "sensorValueId";
  public static final String LABEL_COLLECTION_ID = "labelCollectionId";
  public static final String LABEL_STRING_ID = "labelStringId";

  public static final String LABEL = "label";

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
            .requiredParameterAsCollection(
                    Labels.withId(LABEL_COLLECTION_ID),
                      StaticProperties.stringFreeTextProperty(Labels.withId(LABEL_STRING_ID)))

//            StaticProperties.collection(Labels.withId(PLC_NODES),
//                StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
//                StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_NAME)),
//                StaticProperties.singleValueSelection(Labels.withId(PLC_NODE_TYPE),
//                        Options.from("Bool",  "Byte", "Int", "Word", "Real"))))

            .outputStrategy(OutputStrategies.append(
                    EpProperties.stringEp(Labels.withId(LABEL), LABEL, SPSensor.STATE, PropertyScope.DIMENSION_PROPERTY)
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<NumberLabelerParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String sensorListValueProperty = extractor.mappingPropertyValue(SENSOR_VALUE_ID);

    List<String> statementStrings = extractor.singleValueParameterFromCollection(LABEL_COLLECTION_ID, String.class);

    NumberLabelerParameters params = new NumberLabelerParameters(graph, sensorListValueProperty, statementStrings);

    return new ConfiguredEventProcessor<>(params, NumberLabeler::new);
  }
}

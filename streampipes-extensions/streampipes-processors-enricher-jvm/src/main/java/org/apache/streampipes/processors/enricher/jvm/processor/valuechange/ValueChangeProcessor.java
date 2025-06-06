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

package org.apache.streampipes.processors.enricher.jvm.processor.valuechange;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;

public class ValueChangeProcessor implements IStreamPipesDataProcessor {
  private static final String CHANGE_VALUE_MAPPING_ID = "change-value-mapping";
  private static final String FROM_PROPERTY_VALUE_ID = "from-property-value";
  private static final String TO_PROPERTY_VALUE_ID = "to-property-value";
  private static final String IS_CHANGED_ID = "is-changed";
  private static final String IS_CHANGED = "isChanged";

  private String mappingProperty;
  private float userDefinedFrom;
  private float userDefinedTo;
  private float lastValueOfEvent;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        ValueChangeProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.enricher.jvm.valuechange", 0)
            .category(DataProcessorType.VALUE_OBSERVER)
            .withAssets(ExtensionAssetType.DOCUMENTATION)
            .withLocales(Locales.EN)
            .requiredFloatParameter(Labels.withId(FROM_PROPERTY_VALUE_ID))
            .requiredFloatParameter(Labels.withId(TO_PROPERTY_VALUE_ID))
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(CHANGE_VALUE_MAPPING_ID),
                                    PropertyScope.NONE
                                )
                                .build())
            .outputStrategy(OutputStrategies.append(
                EpProperties.booleanEp(Labels.withId(IS_CHANGED_ID), IS_CHANGED, SO.BOOLEAN)))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) {
    this.lastValueOfEvent = Float.MAX_VALUE;
    this.userDefinedFrom = params.extractor()
                                 .singleValueParameter(FROM_PROPERTY_VALUE_ID, Float.class);
    this.userDefinedTo = params.extractor()
                               .singleValueParameter(TO_PROPERTY_VALUE_ID, Float.class);
    this.mappingProperty = params.extractor()
                                 .mappingPropertyValue(CHANGE_VALUE_MAPPING_ID);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    float thisValue = event.getFieldBySelector(mappingProperty)
                           .getAsPrimitive()
                           .getAsFloat();
    if (this.lastValueOfEvent != Float.MAX_VALUE) {
      event.addField(IS_CHANGED, this.lastValueOfEvent == this.userDefinedFrom && thisValue == this.userDefinedTo);
    } else {
      event.addField(IS_CHANGED, false);
    }

    this.lastValueOfEvent = thisValue;
    spOutputCollector.collect(event);
  }

  @Override
  public void onPipelineStopped() {
  }
}

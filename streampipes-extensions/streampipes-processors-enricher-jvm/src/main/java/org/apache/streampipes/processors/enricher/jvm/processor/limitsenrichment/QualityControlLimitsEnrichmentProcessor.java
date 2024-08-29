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

package org.apache.streampipes.processors.enricher.jvm.processor.limitsenrichment;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class QualityControlLimitsEnrichmentProcessor extends StreamPipesDataProcessor {


  protected static final String UPPER_CONTROL_LIMIT_LABEL = "upperControlLimitInput";
  protected static final String UPPER_WARNING_LIMIT_LABEL = "upperWarningLimitInput";
  protected static final String LOWER_WARNING_LIMIT_LABEL = "lowerWarningLimitInput";
  protected static final String LOWER_CONTROL_LIMIT_LABEL = "lowerControlLimitInput";

  protected static final String UPPER_CONTROL_LIMIT = "upperControlLimit";
  protected static final String UPPER_WARNING_LIMIT = "upperWarningLimit";
  protected static final String LOWER_WARNING_LIMIT = "lowerWarningLimit";
  protected static final String LOWER_CONTROL_LIMIT = "lowerControlLimit";

  private double upperControlLimitValue;
  private double upperWarningLimitValue;
  private double lowerWarningLimitValue;
  private double lowerControlLimitValue;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.enricher.jvm.processor.limitsenrichment", 0)
        .category(DataProcessorType.ENRICH)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
                            .create()
                            .requiredProperty(EpRequirements.anyProperty())
                            .build())
        .requiredFloatParameter(Labels.withId(UPPER_CONTROL_LIMIT_LABEL))
        .requiredFloatParameter(Labels.withId(UPPER_WARNING_LIMIT_LABEL))
        .requiredFloatParameter(Labels.withId(LOWER_WARNING_LIMIT_LABEL))
        .requiredFloatParameter(Labels.withId(LOWER_CONTROL_LIMIT_LABEL))
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.doubleEp(Labels.empty(), UPPER_CONTROL_LIMIT, SO.NUMBER),
                EpProperties.doubleEp(Labels.empty(), UPPER_WARNING_LIMIT, SO.NUMBER),
                EpProperties.doubleEp(Labels.empty(), LOWER_WARNING_LIMIT, SO.NUMBER),
                EpProperties.doubleEp(Labels.empty(), LOWER_CONTROL_LIMIT, SO.NUMBER)
            ))
        .build();
  }

  @Override
  public void onInvocation(
      ProcessorParams parameters,
      SpOutputCollector spOutputCollector,
      EventProcessorRuntimeContext runtimeContext
  ) throws SpRuntimeException {
    this.upperControlLimitValue = parameters.extractor()
                                            .singleValueParameter(UPPER_CONTROL_LIMIT_LABEL, Double.class);
    this.upperWarningLimitValue = parameters.extractor()
                                            .singleValueParameter(UPPER_WARNING_LIMIT_LABEL, Double.class);
    this.lowerWarningLimitValue = parameters.extractor()
                                            .singleValueParameter(LOWER_WARNING_LIMIT_LABEL, Double.class);
    this.lowerControlLimitValue = parameters.extractor()
                                            .singleValueParameter(LOWER_CONTROL_LIMIT_LABEL, Double.class);
  }

  @Override
  public void onDetach() {

  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    event.addField(UPPER_CONTROL_LIMIT, upperControlLimitValue);
    event.addField(UPPER_WARNING_LIMIT, upperWarningLimitValue);
    event.addField(LOWER_WARNING_LIMIT, lowerWarningLimitValue);
    event.addField(LOWER_CONTROL_LIMIT, lowerControlLimitValue);

    collector.collect(event);
  }
}

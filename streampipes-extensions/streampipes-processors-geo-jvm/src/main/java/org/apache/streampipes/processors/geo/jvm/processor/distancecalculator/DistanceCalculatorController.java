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

package org.apache.streampipes.processors.geo.jvm.processor.distancecalculator;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
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
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class DistanceCalculatorController extends StandaloneEventProcessingDeclarer<DistanceCalculatorParameters> {

  private static final String LAT_1_KEY = "lat1";
  private static final String LONG_1_KEY = "long1";
  private static final String LAT_2_KEY = "lat2";
  private static final String LONG_2_KEY = "long2";
  private static final String CALCULATED_DISTANCE_KEY = "calculatedDistance";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.processor.distancecalculator")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT)
                , Labels.withId(LAT_1_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG)
                , Labels.withId(LONG_1_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT)
                , Labels.withId(LAT_2_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG)
                , Labels.withId(LONG_2_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.numberEp(Labels.withId(CALCULATED_DISTANCE_KEY), "distance", SO.NUMBER))
        )
        .build();

  }

  @Override
  public ConfiguredEventProcessor<DistanceCalculatorParameters> onInvocation(
      DataProcessorInvocation sepa,
      ProcessingElementParameterExtractor extractor) {

    String lat1PropertyName = extractor.mappingPropertyValue(LAT_1_KEY);
    String long11PropertyName = extractor.mappingPropertyValue(LONG_1_KEY);
    String lat2PropertyName = extractor.mappingPropertyValue(LAT_2_KEY);
    String long2PropertyName = extractor.mappingPropertyValue(LONG_2_KEY);

    DistanceCalculatorParameters staticParam =
        new DistanceCalculatorParameters(sepa, lat1PropertyName, long11PropertyName, lat2PropertyName,
            long2PropertyName);

    return new ConfiguredEventProcessor<>(staticParam, DistanceCalculator::new);
  }
}

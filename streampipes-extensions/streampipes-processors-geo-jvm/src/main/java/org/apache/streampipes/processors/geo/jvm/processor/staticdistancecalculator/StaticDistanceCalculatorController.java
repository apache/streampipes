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

package org.apache.streampipes.processors.geo.jvm.processor.staticdistancecalculator;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.net.URI;

public class StaticDistanceCalculatorController
    extends StandaloneEventProcessingDeclarer<StaticDistanceCalculatorParameters> {

  private static final String LATITUDE_KEY = "latitude-key";
  private static final String LONGITUDE_KEY = "longitude-key";
  private static final String SELECTED_LATITUDE_KEY = "selected-latitude-key";
  private static final String SELECTED_LONGITUDE_KEY = "selected-longitude-key";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.processor"
            + ".staticdistancecalculator")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat)
                , Labels.withId(LATITUDE_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lng)
                , Labels.withId(LONGITUDE_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredFloatParameter(Labels.withId(SELECTED_LATITUDE_KEY))
        .requiredFloatParameter(Labels.withId(SELECTED_LONGITUDE_KEY))
        .outputStrategy(
            OutputStrategies.append(PrimitivePropertyBuilder
                .create(Datatypes.Float, "distance")
                .domainProperty(SO.Number)
                .measurementUnit(URI.create("http://qudt.org/vocab/unit#Kilometer"))
                .build())
        )
        .build();

  }

  @Override
  public ConfiguredEventProcessor<StaticDistanceCalculatorParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {
    String latitudeFieldName = extractor.mappingPropertyValue(LATITUDE_KEY);
    String longitudeFieldName = extractor.mappingPropertyValue(LONGITUDE_KEY);
    Float selectedLatitude = extractor.singleValueParameter(SELECTED_LATITUDE_KEY, Float.class);
    Float selectedLongitude = extractor.singleValueParameter(SELECTED_LONGITUDE_KEY, Float.class);

    StaticDistanceCalculatorParameters staticParam = new StaticDistanceCalculatorParameters(graph,
        latitudeFieldName, longitudeFieldName, selectedLatitude, selectedLongitude);

    return new ConfiguredEventProcessor<>(staticParam, StaticDistanceCalculator::new);
  }
}

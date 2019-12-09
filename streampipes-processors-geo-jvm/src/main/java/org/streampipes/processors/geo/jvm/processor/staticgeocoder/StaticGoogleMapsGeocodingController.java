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
package org.streampipes.processors.geo.jvm.processor.staticgeocoder;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.vocabulary.Geo;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class StaticGoogleMapsGeocodingController extends StandaloneEventProcessingDeclarer<StaticGoogleMapsGeocodingParameters> {

  private static final String PLACE = "place";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.geo.jvm.staticgeocoding")
            .category(DataProcessorType.ENRICH)
            .withAssets(Assets.DOCUMENTATION)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredTextParameter(Labels.withId("place"))
            .outputStrategy(OutputStrategies.append(
                    EpProperties.doubleEp(Labels.empty(), "latitude", Geo.lat),
                    EpProperties.stringEp(Labels.empty(), "longitude", Geo.lng)
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<StaticGoogleMapsGeocodingParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String place = extractor.singleValueParameter(PLACE, String.class);
    String googleMapsApiKey = GeoJvmConfig.INSTANCE.getGoogleApiKey();

    StaticGoogleMapsGeocodingParameters params = new StaticGoogleMapsGeocodingParameters(graph, place,
            googleMapsApiKey);

    return new ConfiguredEventProcessor<>(params, StaticGoogleMapsGeocoder::new);
  }
}

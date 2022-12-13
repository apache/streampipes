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
package org.apache.streampipes.processors.geo.jvm.processor.revgeocoder;

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
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ReverseGeocodingController extends StandaloneEventProcessingDeclarer<ReverseGeocodingParameters> {

  private static final String LATITUDE_MAPPING = "latitude-mapping";
  private static final String LONGITUDE_MAPPING = "longitude-mapping";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.geo.jvm.reversegeocoding")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat),
                Labels.withId(LATITUDE_MAPPING),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lng),
                Labels.withId(LONGITUDE_MAPPING),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.stringEp(Labels.empty(), "place", "http://schema.org/city")
        ))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<ReverseGeocodingParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {
    String latitudeField = extractor.mappingPropertyValue(LATITUDE_MAPPING);
    String longitudeField = extractor.mappingPropertyValue(LONGITUDE_MAPPING);

    ReverseGeocodingParameters params = new ReverseGeocodingParameters(graph, latitudeField,
        longitudeField);

    return new ConfiguredEventProcessor<>(params, ReverseGeocoding::new);
  }
}

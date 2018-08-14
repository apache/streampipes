/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.geo.jvm.processor.geocode;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.Geo;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class GeocodingController extends StandaloneEventProcessingDeclarer<GeocodingParameters> {

  @Override
  public ConfiguredEventProcessor<GeocodingParameters> onInvocation(DataProcessorInvocation
                                                                                  graph) {
    ProcessingElementParameterExtractor extractor = getExtractor(graph);

    String city = SepaUtils.getMappingPropertyName(graph,
            "city", true);

    String street = SepaUtils.getMappingPropertyName(graph,
            "street", true);

    String number = SepaUtils.getMappingPropertyName(graph,
            "number", true);

    GeocodingParameters params = new GeocodingParameters(graph, city, street, number);
    return new ConfiguredEventProcessor<>(params, () -> new Geocoder(params));
  }

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("geocoder", "Geocoder", "Geocodes a location based " +
            "on given street, address and street number")
            .iconUrl(GeoJvmConfig.iconBaseUrl + "Location_Icon_HQ.png")

            .requiredStream(
                    StreamRequirementsBuilder.create()
                            .requiredPropertyWithUnaryMapping(
                                    EpRequirements
                                            .domainPropertyReq("http://schema.org/city"),
                                    Labels.from("city", "Stadt", ""),
                                    PropertyScope.NONE)
                            .requiredPropertyWithUnaryMapping(
                                    EpRequirements
                                            .domainPropertyReq("http://schema" +
                                                    ".org/streetAddress"),
                                    Labels.from("street", "Straße", ""),
                                    PropertyScope.NONE)
                            .requiredPropertyWithUnaryMapping(
                                    EpRequirements
                                            .domainPropertyReq("http://schema" +
                                                    ".org/streetNumber"),
                                    Labels.from("number", "Hausnummer", ""),
                                    PropertyScope.NONE)
                            .build())
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.from("latitude",
                    "Latitude", ""),
                    "latitude", Geo.lat), EpProperties.doubleEp(Labels.from("longitude",
                    "Longitude", ""), "longitude", Geo.lng)))

            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }
}

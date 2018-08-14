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

package org.streampipes.processors.geo.jvm.processor.route;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class GoogleRoutingController extends StandaloneEventProcessingDeclarer<GoogleRoutingParameters> {

    /*
        TUTORIAL:
        Here you can change the description of the producer
    */
    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("google-routing", "Google Routing", "Uses the Google" +
                "routing service to calculate a route from start to destination")
                .iconUrl(GeoJvmConfig.iconBaseUrl + "Map_Icon_HQ.png")
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

                .requiredTextParameter(Labels.from("home", "Adresse von Ihrem Zuhause", ""))
                .outputStrategy(OutputStrategies.append(EpProperties.stringEp(Labels.from("kvi", "Distanz", ""),
                        "kvi", "http://kvi.de")))

                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    /*
        TUTORIAL:
        Here you get the Configuration Parameters which the User has entered
    */
    @Override
    public ConfiguredEventProcessor<GoogleRoutingParameters> onInvocation(DataProcessorInvocation graph) {
        ProcessingElementParameterExtractor extractor = getExtractor(graph);

        String city = SepaUtils.getMappingPropertyName(graph,
                "city", true);

        String street = SepaUtils.getMappingPropertyName(graph,
                "street", true);

        String number = SepaUtils.getMappingPropertyName(graph,
                "number", true);

        String home = extractor.singleValueParameter("home", String.class);

        GoogleRoutingParameters params = new GoogleRoutingParameters(graph, city, street, number, home);
        return new ConfiguredEventProcessor<>(params, () -> new GoogleRouting(params));
    }
}

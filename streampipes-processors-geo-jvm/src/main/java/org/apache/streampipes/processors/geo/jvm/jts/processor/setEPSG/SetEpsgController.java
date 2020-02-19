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

package org.apache.streampipes.processors.geo.jvm.jts.processor.setEPSG;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;
import org.apache.streampipes.sdk.utils.Assets;

public class SetEpsgController extends StandaloneEventProcessingDeclarer<SetEpsgParameter> {

    public final static String EPSG = "EPSG";
    public final static String EPA_NAME = "EPSG Enricher";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder
                .create("org.apache.streampipes.processors.geo.jvm.jts.processor.setEPSG",
                        EPA_NAME,
                        "Adds an EPSG Code to the event")
                .category(DataProcessorType.GEO)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredStream
                        (StreamRequirementsBuilder
                                .create()
                                .build())
                .requiredIntegerParameter(
                        Labels.from(
                                EPSG,
                                "Sets EPSG Code",
                                "Sets an EPSG Code. Default ist WGS84/WGS84 with number 4326"),
                        4326)
                .outputStrategy(
                        OutputStrategies.append(
                                EpProperties.numberEp(
                                        Labels.from(
                                                "EPSG Code",
                                                "EPSG Code",
                                                "EPSG Code for SRID"),
                                        EPSG, SO.Number)))
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public ConfiguredEventProcessor<SetEpsgParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        Integer epsg_value = extractor.singleValueParameter(EPSG, Integer.class);
        SetEpsgParameter params = new SetEpsgParameter(graph, epsg_value);

        return new ConfiguredEventProcessor<>(params, SetEPSG::new);
    }
}

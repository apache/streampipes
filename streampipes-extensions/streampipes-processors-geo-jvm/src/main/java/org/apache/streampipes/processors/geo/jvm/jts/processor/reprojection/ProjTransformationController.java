/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;
import org.apache.streampipes.sdk.utils.Assets;

public class ProjTransformationController extends StandaloneEventProcessingDeclarer<ProjTransformationParameter> {

    public final static String WKT_KEY = "wkt-key";
    public final static String SOURCE_EPSG_KEY = "source-epsg-key";
    public final static String TARGET_EPSG_KEY = "target_epsg-key";

    public final static String EPA_NAME = "Geometry Reprojection";

    public final static String WKT_RUNTIME = "geomWKT";
    public final static String EPSG_RUNTIME = "epsg";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder
                .create("org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection")
                .category(DataProcessorType.GEO)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(
                                EpRequirements.domainPropertyReq("http://www.opengis.net/ont/geosparql#Geometry"),
                                Labels.withId(WKT_KEY), PropertyScope.MEASUREMENT_PROPERTY
                        )
                        .requiredPropertyWithUnaryMapping(
                                EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                                Labels.withId(SOURCE_EPSG_KEY), PropertyScope.MEASUREMENT_PROPERTY
                        )
                        .build()
                )
                .requiredIntegerParameter(
                        Labels.withId(TARGET_EPSG_KEY),
                        32632
                )
                .outputStrategy(OutputStrategies.keep())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }


    @Override
    public ConfiguredEventProcessor<ProjTransformationParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        String wkt_String = extractor.mappingPropertyValue(WKT_KEY);
        String source_epsg = extractor.mappingPropertyValue(SOURCE_EPSG_KEY);
        Integer target_epsg = extractor.singleValueParameter(TARGET_EPSG_KEY, Integer.class);

        ProjTransformationParameter params = new ProjTransformationParameter(graph, wkt_String, source_epsg, target_epsg);

        return new ConfiguredEventProcessor<>(params, ProjTransformation::new);
    }
}

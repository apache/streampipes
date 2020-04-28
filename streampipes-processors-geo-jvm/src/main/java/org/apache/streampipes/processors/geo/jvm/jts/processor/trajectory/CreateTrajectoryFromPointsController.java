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

package org.apache.streampipes.processors.geo.jvm.jts.processor.trajectory;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class CreateTrajectoryFromPointsController extends  StandaloneEventProcessingDeclarer<CreateTrajectoryFromPointsParameter> {



    public final static String WKT = "trajectory_wkt";
    public final static String EPSG = "EPSG";
    public final static String M = "M-Value";
    public final static String DESCRIPTION = "description";
    public final static String SUBPOINTS = "subpoints";

    public final static String EPA_NAME = "Create Single Trajectory";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder
                .create("org.apache.streampipes.processors.geo.jvm.jts.processor.trajectory",
                        EPA_NAME,
                        "Creates a trajectory from Points")
                .category(DataProcessorType.GEO)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredStream(
                        StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.stringReq(),
                                        Labels.from(WKT,
                                                "Geometry WKT",
                                                "WKT of the requested Geometry"),
                                        PropertyScope.NONE
                                )
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.numberReq(),
                                        Labels.from(EPSG, "EPSG Field", "EPSG Code for SRID"),
                                        PropertyScope.NONE
                                )
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.numberReq(),
                                        Labels.from(M, "M Value", "Choose a value add to trajectory"),
                                        PropertyScope.NONE
                                )
                                .build()
                )
                .requiredTextParameter(
                        Labels.from(
                                DESCRIPTION,
                                "description of trajectory",
                                "Add a description for the trajectory")
                )
                .requiredIntegerParameter(
                        Labels.from(
                                SUBPOINTS,
                                "number of allowed subpoints",
                                "Number og allowed subpoints of the trajector"),
                        2, 10, 1
                )
                .outputStrategy(OutputStrategies.append(
                        EpProperties.stringEp(
                                Labels.from(
                                        "trajectory_wkt",
                                        "trajectory_wkt",
                                        "trajectory wkt (lineString) of a point stream"),
                                WKT,
                                SO.Text),
                        EpProperties.stringEp(
                                Labels.from(
                                        "trajectory_description",
                                        "trajectory_description",
                                        "description of trajectory"),
                                DESCRIPTION,
                                SO.Text))
                )

                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }


    @Override
    public ConfiguredEventProcessor<CreateTrajectoryFromPointsParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {


        String wkt = extractor.mappingPropertyValue(WKT);
        String epsg_value = extractor.mappingPropertyValue(EPSG);
        String m = extractor.mappingPropertyValue(M);

        String description = extractor.singleValueParameter(DESCRIPTION, String.class);
        Integer subpoints = extractor.singleValueParameter(SUBPOINTS, Integer.class);


        CreateTrajectoryFromPointsParameter params = new CreateTrajectoryFromPointsParameter(graph, wkt, epsg_value, description, subpoints, m);

        return new ConfiguredEventProcessor<>(params, CreateTrajectoryFromPoints::new);
    }
}

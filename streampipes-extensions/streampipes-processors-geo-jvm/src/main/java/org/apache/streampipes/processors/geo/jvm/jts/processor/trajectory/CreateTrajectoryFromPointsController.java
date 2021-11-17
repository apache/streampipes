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

public class CreateTrajectoryFromPointsController extends StandaloneEventProcessingDeclarer<CreateTrajectoryFromPointsParameter> {


  public final static String POINT_KEY = "point-key";
  public final static String EPSG_KEY = "epsg-key";
  public final static String M_KEY = "m-key";
  public final static String DESCRIPTION_KEY = "description-key";
  public final static String SUBPOINTS_KEY = "subpoints-key";

  public final static String WKT_KEY = "trajectory-key";
  public final static String WKT_RUNTIME = "trajectoryWKT";

  public final static String DESCRIPTION_RUNTIME = "trajectoryDescription";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.jts.processor.trajectory")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq("http://www.opengis.net/ont/geosparql#Geometry"),
                    Labels.withId(POINT_KEY), PropertyScope.MEASUREMENT_PROPERTY
                )
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                    Labels.withId(EPSG_KEY), PropertyScope.MEASUREMENT_PROPERTY
                )
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.numberReq(),
                    Labels.withId(M_KEY), PropertyScope.MEASUREMENT_PROPERTY
                )
                .build()
        )
        .requiredTextParameter(
            Labels.withId(DESCRIPTION_KEY)
        )
        .requiredIntegerParameter(
            Labels.withId(SUBPOINTS_KEY),
            2, 30, 1
        )

        .outputStrategy(OutputStrategies.append(
            EpProperties.stringEp(
                Labels.withId(DESCRIPTION_KEY),
                DESCRIPTION_RUNTIME,
                SO.Text
            ),
            EpProperties.stringEp(
                Labels.withId(WKT_KEY),
                WKT_RUNTIME,
                "http://www.opengis.net/ont/geosparql#Geometry")
            )
        )
        .build();
  }


  @Override
  public ConfiguredEventProcessor<CreateTrajectoryFromPointsParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {


    String wkt = extractor.mappingPropertyValue(POINT_KEY);
    String epsg = extractor.mappingPropertyValue(EPSG_KEY);
    String m = extractor.mappingPropertyValue(M_KEY);

    String description = extractor.singleValueParameter(DESCRIPTION_KEY, String.class);
    Integer subpoints = extractor.singleValueParameter(SUBPOINTS_KEY, Integer.class);


    CreateTrajectoryFromPointsParameter params = new CreateTrajectoryFromPointsParameter(graph, wkt, epsg, description, subpoints, m);

    return new ConfiguredEventProcessor<>(params, CreateTrajectoryFromPoints::new);
  }
}

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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpTrajectoryBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrajectoryFromPointsProcessor extends StreamPipesDataProcessor {
  private static final String POINT_KEY = "point-key";
  private static final String EPSG_KEY = "epsg-key";
  private static final String M_KEY = "m-key";
  private static final String SUBPOINTS_KEY = "subpoints-key";
  private static final String DESCRIPTION_KEY = "description-key";
  private static final String TRAJECTORY_KEY = "trajectory-key";
  private static final String TRAJECTORY_GEOMETRY_RUNTIME = "trajectory-geometry";
  private static final String TRAJECTORY_EPSG_RUNTIME = "trajectory-epsg";
  private static final String DESCRIPTION_RUNTIME = "trajectory-description";
  private String pointMapper;
  private String epsgMapper;
  private String mValueMapper;
  private String description;
  private Integer subpoints;
  private SpTrajectoryBuilder trajectory;
  private static final Logger LOG = LoggerFactory.getLogger(TrajectoryFromPointsProcessor.class);

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
                .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq
                        ("http://www.opengis.net/ont/geosparql#Geometry"),
                    Labels.withId(POINT_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq
                        ("http://data.ign.fr/def/ignf#CartesianCS"),
                    Labels.withId(EPSG_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.numberReq(),
                    Labels.withId(M_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .build()
        )
        .requiredTextParameter(
            Labels.withId(DESCRIPTION_KEY))
        .requiredIntegerParameter(
            Labels.withId(SUBPOINTS_KEY),
            2, 30, 1)

        .outputStrategy(OutputStrategies.append(
                EpProperties.stringEp(
                    Labels.withId(DESCRIPTION_KEY),
                    DESCRIPTION_RUNTIME,
                    SO.TEXT),
                EpProperties.stringEp(
                    Labels.withId(TRAJECTORY_KEY),
                    TRAJECTORY_GEOMETRY_RUNTIME,
                    "http://www.opengis.net/ont/geosparql#Geometry"),
                EpProperties.integerEp(
                    Labels.withId(EPSG_KEY),
                    TRAJECTORY_EPSG_RUNTIME,
                "http://data.ign.fr/def/ignf#CartesianCS")
            )
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.pointMapper = parameters.extractor().mappingPropertyValue(POINT_KEY);
    this.mValueMapper = parameters.extractor().mappingPropertyValue(M_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);
    this.description = parameters.extractor().singleValueParameter(DESCRIPTION_KEY, String.class);
    this.subpoints = parameters.extractor().singleValueParameter(SUBPOINTS_KEY, Integer.class);

    trajectory = new SpTrajectoryBuilder(subpoints, description);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    // extract values
    String point = event.getFieldBySelector(pointMapper).getAsPrimitive().getAsString();
    Integer epsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();
    Double m = event.getFieldBySelector(mValueMapper).getAsPrimitive().getAsDouble();
    //create JTS geometry
    Point eventGeom = (Point) SpGeometryBuilder.createSPGeom(point, epsg);
    LOG.debug("Geometry Point created");
    //adds point and m value to trajectory object
    trajectory.addPointToTrajectory(eventGeom, m);
    LOG.debug("Point added to trajectory");
    // returns JTS LineString
    LineString geom = trajectory.returnAsLineString(eventGeom.getFactory());
    // adds to stream
    event.addField(DESCRIPTION_RUNTIME, trajectory.getDescription());
    event.addField(TRAJECTORY_GEOMETRY_RUNTIME, geom.toString());
    event.addField(TRAJECTORY_EPSG_RUNTIME, epsg);
    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}

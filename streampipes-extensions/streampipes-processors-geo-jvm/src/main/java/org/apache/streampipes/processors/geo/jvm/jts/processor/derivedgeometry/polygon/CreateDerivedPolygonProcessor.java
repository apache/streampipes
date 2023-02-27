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

package org.apache.streampipes.processors.geo.jvm.jts.processor.derivedgeometry.polygon;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.management.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpNotSupportedGeometryException;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDerivedPolygonProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geometry-key";
  public static final String EPSG_KEY = "epsg-key";
  public static final String POLYGON_OUTPUT_TYPE_KEY = "polygon-output-type-key";
  public static final String DERIVED_GEOM_KEY = "derived-point-geom-key";
  public static final String DERIVED_EPSG_KEY = "derived-point-epsg-key";
  public static final String DERIVED_GEOM_RUNTIME = "derived-polygon";
  public static final String DERIVED_EPSG_RUNTIME = "epsg-derived-polygon";
  private String geometryMapper;
  private String epsgMapper;
  private String outputType;
  ProcessorParams params;
  private static final Logger LOG = LoggerFactory.getLogger(CreateDerivedPolygonProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.geo.jvm.jts.processor.derivedgeometry.polygon")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON, "derivedPolygon.png")
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq("http://www.opengis.net/ont/geosparql#Geometry"),
                Labels.withId(GEOM_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                Labels.withId(EPSG_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredSingleValueSelection(Labels.withId(POLYGON_OUTPUT_TYPE_KEY),
            Options.from("Bounding Box", "Convex Hull"))
        .outputStrategy(OutputStrategies.append(
                EpProperties.stringEp(
                    Labels.withId(DERIVED_GEOM_KEY),
                    DERIVED_GEOM_RUNTIME,
                    "http://www.opengis.net/ont/geosparql#Geometry"),
                EpProperties.integerEp(
                    Labels.withId(DERIVED_EPSG_KEY),
                    DERIVED_EPSG_RUNTIME,
                    "http://data.ign.fr/def/ignf#CartesianCS")
            )
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.geometryMapper = parameters.extractor().mappingPropertyValue(GEOM_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);
    this.outputType = parameters.extractor().selectedSingleValue(POLYGON_OUTPUT_TYPE_KEY, String.class);
    this.params = parameters;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer epsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();
    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, epsg);
    if (geometry instanceof Point) {
      SpMonitoringManager.INSTANCE.addErrorMessage(params.getGraph().getElementId(),
          SpLogEntry.from(System.currentTimeMillis(),
              StreamPipesErrorMessage.from(new SpNotSupportedGeometryException(
                  "Point Geometry is not supported"))));
    } else {
      Polygon derivedPolygonOutput = null;
      switch (this.outputType) {
        case "Bounding Box":
          derivedPolygonOutput = (Polygon) SpGeometryBuilder.createSPGeom(geometry.getEnvelope(), geometry.getSRID());
          break;
        case "Convex Hull":
          derivedPolygonOutput = (Polygon) SpGeometryBuilder.createSPGeom(geometry.convexHull(), geometry.getSRID());
          break;
      }
      event.addField(DERIVED_GEOM_RUNTIME, derivedPolygonOutput.toText());
      event.addField(DERIVED_EPSG_RUNTIME, derivedPolygonOutput.getSRID());
      collector.collect(event);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}

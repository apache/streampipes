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

package org.apache.streampipes.processors.geo.jvm.jts.processor.latlngtojtspoint;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatLngToJtsPointProcessor extends StreamPipesDataProcessor {
  private static final String LAT_KEY = "latitude-key";
  private static final String LNG_KEY = "longitude-key";
  private static final String EPSG_KEY = "epsg-key";
  private static final String GEOMETRY_RUNTIME = "geometry";
  private String latitudeMapper;
  private String longitudeMapper;
  private String epsgMapper;
  private static final Logger LOG = LoggerFactory.getLogger(LatLngToJtsPointProcessor.class);
  public static final String EPA_NAME = "Latitude Longitude To JTS Point";
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.jts.processor.latlngtojtspoint")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT),
                    Labels.withId(LAT_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq(Geo.LNG),
                    Labels.withId(LNG_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                    Labels.withId(EPSG_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .build()
        )
        .outputStrategy(
            OutputStrategies.append(
                PrimitivePropertyBuilder
                    .create(Datatypes.String, GEOMETRY_RUNTIME)
                    .domainProperty("http://www.opengis.net/ont/geosparql#Geometry")
                    .build()
            )
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.latitudeMapper = parameters.extractor().mappingPropertyValue(LAT_KEY);
    this.longitudeMapper = parameters.extractor().mappingPropertyValue(LNG_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    Double lat = event.getFieldBySelector(latitudeMapper).getAsPrimitive().getAsDouble();
    Double lng = event.getFieldBySelector(longitudeMapper).getAsPrimitive().getAsDouble();
    Integer epsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();

    Point geom = SpGeometryBuilder.createSPGeom(lng, lat, epsg);

    if (!geom.isEmpty()) {
      event.addField(GEOMETRY_RUNTIME, geom.toString());

      LOG.debug("Created Geometry: " + geom.toString());
      collector.collect(event);
    } else {
      LOG.warn("An empty point geometry in " + EPA_NAME + " is created due"
          + "invalid input field. Latitude: " + lat + "Longitude: " + lng);
      LOG.error("An event is filtered out because of invalid geometry");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}

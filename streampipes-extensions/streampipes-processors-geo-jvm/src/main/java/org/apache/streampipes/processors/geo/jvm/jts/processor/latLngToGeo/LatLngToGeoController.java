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

package org.apache.streampipes.processors.geo.jvm.jts.processor.latLngToGeo;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class LatLngToGeoController extends StandaloneEventProcessingDeclarer<LatLngToGeoParameter> {


  public final static String LAT_KEY = "latitude-key";
  public final static String LNG_KEY = "longitude-key";
  public final static String EPSG_KEY = "epsg-key";

  public final static String WKT_RUNTIME = "geomWKT";
  public final static String EPA_NAME = "Create Point from Latitude and Longitude";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.jts.processor.latLngToGeo")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat),
                    Labels.withId(LAT_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq(Geo.lng),
                    Labels.withId(LNG_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                    Labels.withId(EPSG_KEY), PropertyScope.MEASUREMENT_PROPERTY)
                .build()
        )
        .outputStrategy(
            OutputStrategies.append(
                PrimitivePropertyBuilder
                    .create(Datatypes.String, WKT_RUNTIME)
                    .domainProperty("http://www.opengis.net/ont/geosparql#Geometry")
                    .build())
        )
        .build();
  }

  @Override
  public ConfiguredEventProcessor<LatLngToGeoParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String lat = extractor.mappingPropertyValue(LAT_KEY);
    String lng = extractor.mappingPropertyValue(LNG_KEY);
    String epsg = extractor.mappingPropertyValue(EPSG_KEY);

    LatLngToGeoParameter params = new LatLngToGeoParameter(graph, epsg, lat, lng);

    return new ConfiguredEventProcessor<>(params, LatLngToGeo::new);
  }
}

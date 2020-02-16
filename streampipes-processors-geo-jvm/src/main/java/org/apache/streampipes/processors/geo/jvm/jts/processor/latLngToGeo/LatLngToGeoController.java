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
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class LatLngToGeoController extends  StandaloneEventProcessingDeclarer<LatLngToGeoParameter> {


    public final static String LAT_FIELD = "lat_field";
    public final static String LNG_FIELD = "lng_field";
    public final static String EPSG = "EPSG";
    public final static String WKT = "geom_wkt";
    public final static String EPA_NAME = "Create Point from Latitude and Longitude";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder
                .create("org.apache.streampipes.processors.geo.jvm.jts.processor.latLngToGeo",
                        EPA_NAME,
                        "Creates a point geometry from Latitude and Longitude values")
                .category(DataProcessorType.GEO)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredStream(
                        StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.numberReq(),
                                        Labels.from(LAT_FIELD,
                                                "Latitude field",
                                                "Latitude value"),
                                        PropertyScope.NONE
                                )
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.numberReq(),
                                        Labels.from(LNG_FIELD,
                                                "Longitude field",
                                                "Longitude value"),
                                        PropertyScope.NONE
                                )
                                .requiredPropertyWithUnaryMapping(
                                        EpRequirements.numberReq(),
                                        Labels.from(EPSG, "EPSG Field", "EPSG Code for SRID"),
                                        PropertyScope.NONE
                                )
                                .build()
                )
                .outputStrategy(OutputStrategies.append(EpProperties.stringEp(
                        Labels.from(
                                "point_wkt",
                                "wkt",
                                "wkt point from long lat values"),
                    WKT,
                        SO.Text))
                )

                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }


    @Override
    public ConfiguredEventProcessor<LatLngToGeoParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {


        String lat = extractor.mappingPropertyValue(LAT_FIELD);
        String lng = extractor.mappingPropertyValue(LNG_FIELD);
        String epsg_value = extractor.mappingPropertyValue(EPSG);

        LatLngToGeoParameter params = new LatLngToGeoParameter(graph, epsg_value, lat, lng);

        return new ConfiguredEventProcessor<>(params, LatLngToGeo::new);
    }
}

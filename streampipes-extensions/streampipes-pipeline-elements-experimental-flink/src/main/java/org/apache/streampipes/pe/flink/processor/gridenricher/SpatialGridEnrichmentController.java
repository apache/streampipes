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

package org.apache.streampipes.pe.flink.processor.gridenricher;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class SpatialGridEnrichmentController extends FlinkDataProcessorDeclarer<SpatialGridEnrichmentParameters> {

  private static final String MAPPING_LATITUDE = "mapping-latitude";
  private static final String MAPPING_LONGITUDE = "mapping-longitude";

  private static final String CELLSIZE = "cellSize";
  private static final String STARTING_CELL = "startingCell";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.geo.flink")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT)
                , Labels.withId(MAPPING_LATITUDE), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG)
                , Labels.withId(MAPPING_LONGITUDE), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_X_KEY, SO.NUMBER),
            EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_Y_KEY, SO.NUMBER),
            EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LAT_NW_KEY, Geo.LAT),
            EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LON_NW_KEY, Geo.LNG),
            EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LAT_SE_KEY, Geo.LAT),
            EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LON_SE_KEY, Geo.LNG),
            EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_CELLSIZE_KEY, SO.NUMBER)))
        .requiredIntegerParameter(Labels.withId(CELLSIZE),
            100, 10000, 100)
        .requiredOntologyConcept(Labels.withId(STARTING_CELL), StaticProperties
            .supportedDomainProperty(Geo.LAT, true), StaticProperties
            .supportedDomainProperty(Geo.LNG, true))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<SpatialGridEnrichmentParameters> getProgram(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {

    Integer cellSize = extractor.singleValueParameter(CELLSIZE, Integer.class);
    String latitudePropertyName = extractor.mappingPropertyValue(MAPPING_LATITUDE);
    String longitudePropertyName = extractor.mappingPropertyValue(MAPPING_LONGITUDE);

    Double startingLatitude = extractor.supportedOntologyPropertyValue(STARTING_CELL, Geo.LAT,
        Double.class);

    Double startingLongitude = extractor.supportedOntologyPropertyValue(STARTING_CELL, Geo.LNG,
        Double.class);

    EnrichmentSettings enrichmentSettings = new EnrichmentSettings(
        startingLatitude, startingLongitude,
        cellSize,
        latitudePropertyName,
        longitudePropertyName);

    SpatialGridEnrichmentParameters params = new SpatialGridEnrichmentParameters(graph,
        enrichmentSettings);

    return new SpatialGridEnrichmentProgram(params);

  }
}

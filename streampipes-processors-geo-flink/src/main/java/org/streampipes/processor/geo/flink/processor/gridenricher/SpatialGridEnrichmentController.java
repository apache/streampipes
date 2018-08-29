/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processor.geo.flink.processor.gridenricher;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processor.geo.flink.config.GeoFlinkConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.Geo;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class SpatialGridEnrichmentController extends FlinkDataProcessorDeclarer<SpatialGridEnrichmentParameters> {

  private static final String MAPPING_LATITUDE = "mapping-latitude";
  private static final String MAPPING_LONGITUDE = "mapping-longitude";

  private static final String CELLSIZE = "cellsize";
  private static final String STARTING_CELL = "starting-cell";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.geo.flink", "Spatial Grid Enrichment", "Groups spatial " +
            "events into cells of a given size")
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat)
                            , Labels.from(MAPPING_LATITUDE, "Latitude Property", ""), PropertyScope.MEASUREMENT_PROPERTY)
                    .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lng)
                            , Labels.from(MAPPING_LONGITUDE, "Longitude Property", ""), PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_X_KEY, SO.Number),
                    EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_Y_KEY, SO.Number),
                    EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LAT_NW_KEY, Geo.lat),
                    EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LON_NW_KEY, Geo.lng),
                    EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LAT_SE_KEY, Geo.lat),
                    EpProperties.doubleEp(Labels.empty(), SpatialGridConstants.GRID_LON_SE_KEY, Geo.lng),
                    EpProperties.integerEp(Labels.empty(), SpatialGridConstants.GRID_CELLSIZE_KEY, SO.Number)))
            .requiredIntegerParameter(CELLSIZE, "Cell Size", "The size of a cell in meters",
                    100, 10000, 100)
            .requiredOntologyConcept(Labels.from(STARTING_CELL, "Starting Location", "The " +
                    "upper-left corner of the starting cell"), StaticProperties
                    .supportedDomainProperty(Geo.lat, true), StaticProperties
                    .supportedDomainProperty(Geo.lng, true))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<SpatialGridEnrichmentParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    Integer cellSize = extractor.singleValueParameter(CELLSIZE, Integer.class);
    String latitudePropertyName = extractor.mappingPropertyValue(MAPPING_LATITUDE);
    String longitudePropertyName = extractor.mappingPropertyValue(MAPPING_LONGITUDE);

    Double startingLatitude = extractor.supportedOntologyPropertyValue(STARTING_CELL, Geo.lat,
            Double.class);

    Double startingLongitude = extractor.supportedOntologyPropertyValue(STARTING_CELL, Geo.lng,
            Double.class);

    EnrichmentSettings enrichmentSettings = new EnrichmentSettings(
            startingLatitude, startingLongitude,
            cellSize,
            latitudePropertyName,
            longitudePropertyName);

    SpatialGridEnrichmentParameters params = new SpatialGridEnrichmentParameters(graph,
            enrichmentSettings);

    return new SpatialGridEnrichmentProgram(params, GeoFlinkConfig.INSTANCE.getDebug());

  }
}

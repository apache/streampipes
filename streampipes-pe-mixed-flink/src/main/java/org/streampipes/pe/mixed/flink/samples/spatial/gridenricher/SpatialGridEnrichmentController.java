package org.streampipes.pe.mixed.flink.samples.spatial.gridenricher;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridEnrichmentController extends AbstractFlinkAgentDeclarer<SpatialGridEnrichmentParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("grid", "Spatial Grid Enrichment", "Groups spatial " +
            "events into cells of a given size")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat)
                    , "mapping-latitude", "Latitude Property", "")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lng)
                    , "mapping-longitude", "Longitude Property", "")
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.integerEp(SpatialGridConstants.GRID_X_KEY, SO.Number),
                    EpProperties.integerEp(SpatialGridConstants.GRID_Y_KEY, SO.Number),
                    EpProperties.doubleEp(SpatialGridConstants.GRID_LAT_NW_KEY, Geo.lat),
                    EpProperties.doubleEp(SpatialGridConstants.GRID_LON_NW_KEY, Geo.lng),
                    EpProperties.doubleEp(SpatialGridConstants.GRID_LAT_SE_KEY, Geo.lat),
                    EpProperties.doubleEp(SpatialGridConstants.GRID_LON_SE_KEY, Geo.lng),
                    EpProperties.integerEp(SpatialGridConstants.GRID_CELLSIZE_KEY, SO.Number)))
            .requiredIntegerParameter("cellsize", "Cell Size", "The size of a cell in meters",
                    100, 10000, 100)
            .requiredOntologyConcept(Labels.from("starting-cell", "Starting Location", "The " +
                    "upper-left corner of the starting cell"), StaticProperties
                    .supportedDomainProperty(Geo.lat, true), StaticProperties
                    .supportedDomainProperty(Geo.lng, true))
            .build();
  }

  @Override
  protected FlinkSepaRuntime<SpatialGridEnrichmentParameters> getRuntime(SepaInvocation graph) {

    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(graph);

    Integer cellSize = extractor.singleValueParameter("cellsize", Integer.class);
    String latitudePropertyName = extractor.mappingPropertyValue("mapping-latitude");
    String longitudePropertyName = extractor.mappingPropertyValue("mapping-longitude");

    Double startingLatitude = extractor.supportedOntologyPropertyValue("starting-cell", Geo.lat,
            Double.class);

    Double startingLongitude = extractor.supportedOntologyPropertyValue("starting-cell", Geo.lng,
            Double.class);

    EnrichmentSettings enrichmentSettings = new EnrichmentSettings(
            startingLatitude, startingLongitude,
            cellSize,
            latitudePropertyName,
            longitudePropertyName);

    SpatialGridEnrichmentParameters params = new SpatialGridEnrichmentParameters(graph,
            enrichmentSettings);

    return new SpatialGridEnrichmentProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//    return new SpatialGridEnrichmentProgram(params);

  }
}

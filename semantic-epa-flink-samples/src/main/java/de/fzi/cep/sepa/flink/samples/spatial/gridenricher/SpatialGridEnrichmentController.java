package de.fzi.cep.sepa.flink.samples.spatial.gridenricher;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.StaticProperties;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.Labels;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridEnrichmentController extends AbstractFlinkAgentDeclarer<SpatialGridEnrichmentParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("grid", "Spatial Grid Enrichment", "Groups spatial " +
            "events into cells of a given size")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat)
                    , "mapping-latitude", "Latitude Property", "")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lng)
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

    return new SpatialGridEnrichmentProgram(params, makeDeploymentConfig(Config.JAR_FILE));
//    return new SpatialGridEnrichmentProgram(params);

  }
}

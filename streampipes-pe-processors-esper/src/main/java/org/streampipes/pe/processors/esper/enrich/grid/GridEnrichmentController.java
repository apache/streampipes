package org.streampipes.pe.processors.esper.enrich.grid;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OntologyProperties;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.vocabulary.Geo;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class GridEnrichmentController extends StandaloneEventProcessorDeclarerSingleton<GridEnrichmentParameter> {

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("grid", "Grid Cell Grouping",
            "Groups location-based events into cells of a given size")
            .category(DataProcessorType.ENRICH)
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat), "latitude", "Select Latitude Mapping", "")
            .requiredPropertyStream1WithNaryMapping(EpRequirements.domainPropertyReq(Geo.lng), "longitude", "Select Longitude Mapping", "")
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .outputStrategy(OutputStrategies.append(EpProperties.nestedEp(Labels.empty(), "cellOptions", EpProperties
                            .integerEp
                            (Labels.empty(), "cellX", "http://schema.org/Number"),
                    EpProperties.integerEp(Labels.empty(), "cellY", "http://schema.org/Number"),
                    EpProperties.doubleEp(Labels.empty(), "latitudeNW", "http://test.de/latitude"),
                    EpProperties.doubleEp(Labels.empty(), "longitudeNW", "http://test.de/longitude"),
                    EpProperties.doubleEp(Labels.empty(), "latitudeSE", "http://test.de/latitude"),
                    EpProperties.doubleEp(Labels.empty(), "longitudeSE", "http://test.de/longitude"),
                    EpProperties.integerEp(Labels.empty(), "cellSize", "http://schema.org/Number"))))
            .requiredIntegerParameter("cellSize", "The size of a cell in meters", "", 0, 10000, 100)
            .requiredOntologyConcept(Labels.from("startingCell", "Starting cell (upper left corner)", "Select a " +
                    "valid location."), OntologyProperties.mandatory(Geo.lat), OntologyProperties.mandatory(Geo.lng))

            .build();
  }

  @Override
  public ConfiguredEventProcessor<GridEnrichmentParameter> onInvocation
          (DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {

    Integer cellSize =extractor.singleValueParameter("cellSize", Integer.class);
    Double startingLatitude = extractor.supportedOntologyPropertyValue("startingCell", Geo.lat, Double.class);
    Double startingLongitude = extractor.supportedOntologyPropertyValue("startingCell", Geo.lng, Double.class);

    String latPropertyName = extractor.mappingPropertyValue("latitude");
    String lngPropertyName = extractor.mappingPropertyValue("longitude");

    AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);
    String cellOptionsPropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "cellOptions");

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    GridEnrichmentParameter staticParam = new GridEnrichmentParameter(
            sepa,
            startingLatitude, startingLongitude,
            cellSize,
            cellOptionsPropertyName,
            latPropertyName,
            lngPropertyName,
            selectProperties);

    return new ConfiguredEventProcessor<>(staticParam, GridEnrichment::new);
  }
}

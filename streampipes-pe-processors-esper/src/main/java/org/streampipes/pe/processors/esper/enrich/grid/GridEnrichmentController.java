package org.streampipes.pe.processors.esper.enrich.grid;

import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OntologyProperties;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class GridEnrichmentController extends StandaloneEventProcessorDeclarerSingleton<GridEnrichmentParameter> {

  @Override
  public SepaDescription declareModel() {

    return ProcessingElementBuilder.create("grid", "Grid Cell Grouping",
            "Groups location-based events into cells of a given size")
            .category(EpaType.ENRICH)
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.domainPropertyReq(Geo.lat), "latitude", "Select Latitude Mapping", "")
            .requiredPropertyStream1WithNaryMapping(EpRequirements.domainPropertyReq(Geo.lng), "longitude", "Select Longitude Mapping", "")
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .outputStrategy(OutputStrategies.append(EpProperties.nestedEp("cellOptions", EpProperties.integerEp
                            ("cellX", "http://schema.org/Number"),
                    EpProperties.integerEp("cellY", "http://schema.org/Number"),
                    EpProperties.doubleEp("latitudeNW", "http://test.de/latitude"),
                    EpProperties.doubleEp("longitudeNW", "http://test.de/longitude"),
                    EpProperties.doubleEp("latitudeSE", "http://test.de/latitude"),
                    EpProperties.doubleEp("longitudeSE", "http://test.de/longitude"),
                    EpProperties.integerEp("cellSize", "http://schema.org/Number"))))
            .requiredIntegerParameter("cellSize", "The size of a cell in meters", "", 0, 10000, 100)
            .requiredOntologyConcept(Labels.from("startingCell", "Starting cell (upper left corner)", "Select a " +
                    "valid location."), OntologyProperties.mandatory(Geo.lat), OntologyProperties.mandatory(Geo.lng))

            .build();
  }

  @Override
  public ConfiguredEventProcessor<GridEnrichmentParameter, EventProcessor<GridEnrichmentParameter>> onInvocation
          (SepaInvocation sepa) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

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

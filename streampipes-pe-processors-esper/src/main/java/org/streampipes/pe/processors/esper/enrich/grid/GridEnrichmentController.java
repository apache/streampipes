package org.streampipes.pe.processors.esper.enrich.grid;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.PropertyValueSpecification;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.impl.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;
import org.streampipes.container.util.StandardTransportFormat;

public class GridEnrichmentController extends StandaloneEventProcessorDeclarerSingleton<GridEnrichmentParameter> {

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription sepa = new SepaDescription("grid", "Grid Cell Grouping",
				"Groups location-based events into cells of a given size");
		sepa.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		try {	
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
			EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
			eventProperties.add(e1);
			eventProperties.add(e2);
			
			EventStream stream1 = StreamBuilder
					.createStreamRestriction(EsperConfig.serverUrl +"/" + sepa.getElementId())
					.schema(
							SchemaBuilder.create()
								.properties(eventProperties)
								.build()
							).build();
			sepa.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
			List<EventProperty> nestedProperties = new ArrayList<>();
			
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._integer, "cellX", "http://schema.org/Number").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._integer, "cellY", "http://schema.org/Number").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "latitudeNW", "http://test.de/latitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "longitudeNW", "http://test.de/longitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "latitudeSE", "http://test.de/latitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "longitudeSE", "http://test.de/longitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._integer, "cellSize", "http://schema.org/Number").build());
		
			EventProperty cellProperties = new EventPropertyNested("cellOptions", nestedProperties);
			appendProperties.add(cellProperties);

			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			sepa.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			staticProperties.add(StaticProperties.integerFreeTextProperty("cellSize", "The size of a cell in meters", "", new PropertyValueSpecification(0, 10000, 100)));
			
			SupportedProperty sp1 = new SupportedProperty(Geo.lat, true);
			SupportedProperty sp2 = new SupportedProperty(Geo.lng, true);
			DomainStaticProperty sp = new DomainStaticProperty("startingCell", "Starting cell (upper left corner)", "Select a valid location.", Arrays.asList(sp1, sp2));
			staticProperties.add(sp);
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping", ""));
			sepa.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return sepa;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {		
		
		int cellSize = (int) Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(sepa, "cellSize"));
		double startingLatitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(SepaUtils.getDomainStaticPropertyBy(sepa, "startingCell"), Geo.lat));
		double startingLongitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(SepaUtils.getDomainStaticPropertyBy(sepa, "startingCell"), Geo.lng));
		
		String latPropertyName = SepaUtils.getMappingPropertyName(sepa, "latitude");
		String lngPropertyName = SepaUtils.getMappingPropertyName(sepa, "longitude");	
			
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);
		String cellOptionsPropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "cellOptions");
	
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
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

		return submit(staticParam, GridEnrichment::new);

	}
}

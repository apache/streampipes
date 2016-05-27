package de.fzi.cep.sepa.esper.enrich.grid;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StaticProperties;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.PropertyValueSpecification;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class GridEnrichmentController extends FlatEpDeclarer<GridEnrichmentParameter> {

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription sepa = new SepaDescription("sepa/grid", "Grid Cell Grouping",
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
	
		try {
			invokeEPRuntime(staticParam, GridEnrichment::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}

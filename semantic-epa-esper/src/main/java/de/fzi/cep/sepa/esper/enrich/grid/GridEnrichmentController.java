package de.fzi.cep.sepa.esper.enrich.grid;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.*;
import de.fzi.cep.sepa.model.impl.graph.*;
import de.fzi.cep.sepa.model.impl.output.*;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class GridEnrichmentController extends EsperDeclarer<GridEnrichmentParameter> {

	@Override
	public SEPA declareModel() {
		
		SEPA sepa = new SEPA("/sepa/grid", "Grid Cell Grouping",
				"Groups location-based events into cells of a given size", "", "/sepa/grid", Utils.createList(Domain.DOMAIN_PERSONAL_ASSISTANT.toString()));
		sepa.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		try {	
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://test.de/latitude").build();
			EventProperty e2 = PrimitivePropertyBuilder.createPropertyRestriction("http://test.de/longitude").build();
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
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "latitudeNW", "http://schema.org/latitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "longitudeNW", "http://schema.org/longitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "latitudeSE", "http://schema.org/latitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._double, "longitudeSE", "http://schema.org/longitude").build());
			nestedProperties.add(PrimitivePropertyBuilder.createProperty(XSD._integer, "cellSize", "http://schema.org/Number").build());
		
			EventProperty cellProperties = new EventPropertyNested("cellOptions", nestedProperties);
			appendProperties.add(cellProperties);

			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			sepa.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			staticProperties.add(new FreeTextStaticProperty("cellSize", "The size of a cell in meters"));
			staticProperties.add(new FreeTextStaticProperty("startingLatitude", "The latitude value of the center of the first cell"));
			staticProperties.add(new FreeTextStaticProperty("startingLongitude", "The longitude value of the center of the first cell"));
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping"));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping"));
			sepa.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return sepa;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {		
		
		int cellSize = Integer.parseInt(SEPAUtils.getFreeTextStaticPropertyValue(sepa, "cellSize"));
		double startingLatitude = Double.parseDouble(SEPAUtils.getFreeTextStaticPropertyValue(sepa, "startingLatitude"));
		double startingLongitude = Double.parseDouble(SEPAUtils.getFreeTextStaticPropertyValue(sepa, "startingLongitude"));
		
		String latPropertyName = SEPAUtils.getMappingPropertyName(sepa, "latitude");
		String lngPropertyName = SEPAUtils.getMappingPropertyName(sepa, "longitude");	
			
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);
		String cellOptionsPropertyName = SEPAUtils.getEventPropertyName(strategy.getEventProperties(), "cellOptions");
	
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
			return invokeEPRuntime(staticParam, GridEnrichment::new, sepa);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

package de.fzi.cep.sepa.esper.enrich.grid;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;
import org.openrdf.model.impl.GraphImpl;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.storage.util.Transformer;

public class GridEnrichmentController extends EsperDeclarer<GridEnrichmentParameter> {

	private EventProperty xCellProperty;
	private EventProperty yCellProperty;
	
	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		SEPA desc = new SEPA("/sepa/grid", "Grid Cell Grouping",
				"Groups location-based events into cells of a given size", "", "/sepa/grid", domains);
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://test.de/latitude"));
			EventProperty e2 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://test.de/longitude"));
			eventProperties.add(e1);
			eventProperties.add(e2);
			
			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri("http://localhost:8090/" + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
			List<EventProperty> nestedProperties = new ArrayList<>();
			
			xCellProperty = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellX", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
			yCellProperty = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellY", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
			
			EventProperty latitudeNW = new EventPropertyPrimitive(XSD._double.toString(),
					"latitudeNW", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/latitude"));
			EventProperty longitudeNW = new EventPropertyPrimitive(XSD._double.toString(),
					"longitudeNW", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/longitude"));
			
			EventProperty latitudeSE = new EventPropertyPrimitive(XSD._double.toString(),
					"latitudeSE", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/latitude"));
			EventProperty longitudeSE = new EventPropertyPrimitive(XSD._double.toString(),
					"longitudeSE", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/longitude"));
			
			EventProperty cellSize = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellSize", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
			
			nestedProperties.add(xCellProperty);
			nestedProperties.add(yCellProperty);
			nestedProperties.add(latitudeNW);
			nestedProperties.add(longitudeNW);
			nestedProperties.add(latitudeSE);
			nestedProperties.add(longitudeSE);
			nestedProperties.add(cellSize);
			
			EventProperty cellProperties = new EventPropertyNested("cellOptions", nestedProperties);
			appendProperties.add(cellProperties);

			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			staticProperties.add(new FreeTextStaticProperty("cellSize", "The size of a cell in meters"));
			staticProperties.add(new FreeTextStaticProperty("startingLatitude", "The latitude value of the center of the first cell"));
			staticProperties.add(new FreeTextStaticProperty("startingLongitude", "The longitude value of the center of the first cell"));
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping"));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping"));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {		
		/*try {
			System.out.println(Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), sepa)));
		} catch (Exception e )
		{
			
		}*/
		EventStream inputStream = sepa.getInputStreams().get(0);
		
		EventGrounding inputGrounding = inputStream.getEventGrounding();
		EventGrounding outputGrounding = sepa.getOutputStream().getEventGrounding();
		String topicPrefix = "topic://";
		
		String inName = topicPrefix + inputGrounding.getTopicName();
		String outName = topicPrefix + outputGrounding.getTopicName();
		
		int cellSize = Integer.parseInt(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "cellSize"))).getValue());
		
		double startingLatitude = Double.parseDouble(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "startingLatitude"))).getValue());
		
		double startingLongitude = Double.parseDouble(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "startingLongitude"))).getValue());
		
		String latPropertyName = SEPAUtils.getMappingPropertyName(sepa,
				"latitude");
		
		String lngPropertyName = SEPAUtils.getMappingPropertyName(sepa,
				"longitude");	
			
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

		String cellOptionsPropertyName = SEPAUtils.getEventPropertyName(strategy.getEventProperties(), "cellOptions");

		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getPropertyName());
		}
		
		GridEnrichmentParameter staticParam = new GridEnrichmentParameter(
				inName, 
				outName, 
				inputStream.getEventSchema().toPropertyList(), 
				sepa.getOutputStream().getEventSchema().toPropertyList(), 
				startingLatitude, startingLongitude, 
				cellSize, 
				cellOptionsPropertyName, 
				latPropertyName, 
				lngPropertyName,
				selectProperties);
	
		try {
			return runEngine(staticParam, GridEnrichment::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}
	

}

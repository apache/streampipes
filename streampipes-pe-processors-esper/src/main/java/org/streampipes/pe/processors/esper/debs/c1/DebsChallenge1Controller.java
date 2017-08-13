package org.streampipes.pe.processors.esper.debs.c1;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.FixedOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.wrapper.standalone.declarer.FlatEventProcessorDeclarer;

public class DebsChallenge1Controller extends FlatEventProcessorDeclarer<DebsChallenge1Parameters> {

	private EventProperty xCellProperty;
	private EventProperty yCellProperty;
	
	@Override
	public SepaDescription declareModel() {
			SepaDescription desc = new SepaDescription("debs_c1", "DEBS Challenge v1",
				"Solves query 1 of the 2015 Debs Challenge", "");
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
			EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
			EventProperty e3 = EpRequirements.domainPropertyReq(Geo.lat);
			EventProperty e4 = EpRequirements.domainPropertyReq(Geo.lng);
			eventProperties.add(e1);
			eventProperties.add(e2);
			eventProperties.add(e3);
			eventProperties.add(e4);
			
			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri("http://localhost:8090/" + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			FixedOutputStrategy outputStrategy = new FixedOutputStrategy();

			EventPropertyList list = new EventPropertyList();
			list.setRuntimeName("list");
			
			
			List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
			List<EventProperty> nestedProperties = new ArrayList<>();
			
			xCellProperty = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellX", "", Utils.createURI("http://schema.org/Number"));
			yCellProperty = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellY", "", Utils.createURI("http://schema.org/Number"));
			
			EventProperty latitudeNW = new EventPropertyPrimitive(XSD._double.toString(),
					"latitudeNW", "", Utils.createURI("http://test.de/latitude"));
			EventProperty longitudeNW = new EventPropertyPrimitive(XSD._double.toString(),
					"longitudeNW", "", Utils.createURI("http://test.de/longitude"));
			
			EventProperty latitudeSE = new EventPropertyPrimitive(XSD._double.toString(),
					"latitudeSE", "", Utils.createURI("http://test.de/latitude"));
			EventProperty longitudeSE = new EventPropertyPrimitive(XSD._double.toString(),
					"longitudeSE", "", Utils.createURI("http://test.de/longitude"));
			
			EventProperty cellSize = new EventPropertyPrimitive(XSD._integer.toString(),
					"cellSize", "", Utils.createURI("http://schema.org/Number"));
			
			nestedProperties.add(xCellProperty);
			nestedProperties.add(yCellProperty);
			nestedProperties.add(latitudeNW);
			nestedProperties.add(longitudeNW);
			nestedProperties.add(latitudeSE);
			nestedProperties.add(longitudeSE);
			nestedProperties.add(cellSize);
			
			EventProperty cellProperties = new EventPropertyNested("cellOptions", nestedProperties);
			EventProperty cell1Properties = new EventPropertyNested("cellOptions1", nestedProperties);
			EventProperty countValue = new EventPropertyPrimitive(XSD._integer.toString(),
					"countValue", "", Utils.createURI("http://schema.org/Number"));
			
			appendProperties.add(cellProperties);
			appendProperties.add(cell1Properties);
			appendProperties.add(countValue);

			list.setEventProperties(appendProperties);
			
			outputStrategy.setEventProperties(Utils.createList(list));
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			staticProperties.add(new FreeTextStaticProperty("cellSize", "The size of a cell in meters", ""));
			staticProperties.add(new FreeTextStaticProperty("startingLatitude", "The latitude value of the center of the first cell", ""));
			staticProperties.add(new FreeTextStaticProperty("startingLongitude", "The longitude value of the center of the first cell", ""));
			
			// Mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping (Pickup)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping (Pickup)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e3.getElementName()), "latitude2", "Select Latitude Mapping (Dropoff)", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e4.getElementName()), "longitude2", "Select Longitude Mapping (Dropoff)", ""));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		int cellSize = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "cellSize"))).getValue());
		
		double startingLatitude = Double.parseDouble(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "startingLatitude"))).getValue());
		
		double startingLongitude = Double.parseDouble(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "startingLongitude"))).getValue());
		
		String latPropertyName = SepaUtils.getMappingPropertyName(sepa,
				"latitude");
		
		String lngPropertyName = SepaUtils.getMappingPropertyName(sepa,
				"longitude");	
		
		String latProperty2Name = SepaUtils.getMappingPropertyName(sepa,
				"latitude2");
		
		String lngProperty2Name = SepaUtils.getMappingPropertyName(sepa,
				"longitude2");	

		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		DebsChallenge1Parameters staticParam = new DebsChallenge1Parameters(
				sepa, 
				startingLatitude, startingLongitude, 
				cellSize, 
				latPropertyName, 
				lngPropertyName,
				latProperty2Name, 
				lngProperty2Name,
				selectProperties);

		return submit(staticParam, DebsChallenge1::new, sepa);

	}
}

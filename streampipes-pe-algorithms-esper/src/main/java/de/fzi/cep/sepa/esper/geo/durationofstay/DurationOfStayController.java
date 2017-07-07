package de.fzi.cep.sepa.esper.geo.durationofstay;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.geo.geofencing.GeofencingData;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.StaticProperties;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
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
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class DurationOfStayController extends FlatEpDeclarer<DurationOfStayParameters>{

	@Override
	public SepaDescription declareModel() {
		EventStream stream1 = new EventStream();
		EventSchema schema = new EventSchema();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e3 = new EventPropertyPrimitive();
		EventProperty e4 = EpRequirements.domainPropertyReq("http://schema.org/DateTime");
		
		schema.setEventProperties(Arrays.asList(e1, e2, e3, e4));
		
		SepaDescription desc = new SepaDescription("durationofstay", "Duration of Stay", "Calculates the duration of stay of a location-based object within a specified radius around a specified point-based coordinate.");
		desc.setCategory(Arrays.asList(EpaType.GEO.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema);
	
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> additionalProperties = new ArrayList<>();
		additionalProperties.add(EpProperties.longEp("departureTime", "http://schema.org/DateTime"));
		additionalProperties.add(EpProperties.longEp("durationOfStay", "http://schema.org/Number"));
		AppendOutputStrategy appendOutput = new AppendOutputStrategy();
		appendOutput.setEventProperties(additionalProperties);
		strategies.add(appendOutput);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		staticProperties.add(StaticProperties.integerFreeTextProperty("radius", "Radius (m)", "Specifies the geofence size (the radius around the provided location) in meters.", new PropertyValueSpecification(0, 10000, 10)));
			
		SupportedProperty latSp = new SupportedProperty(Geo.lat, true);
		SupportedProperty lngSp = new SupportedProperty(Geo.lng, true);
		
		List<SupportedProperty> supportedProperties = Arrays.asList(latSp, lngSp);
		DomainStaticProperty dsp = new DomainStaticProperty("location", "Location", "Specifies the center of the geofence", supportedProperties);
		
		staticProperties.add(dsp);
		
		MappingPropertyUnary latMapping = new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping-latitude", "Latitude Coordinate", "Specifies the latitude field of the stream.");
		staticProperties.add(latMapping);
		
		MappingPropertyUnary lngMapping = new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping-longitude", "Longitude Coordinate", "Specifies the longitude field of the stream.");
		staticProperties.add(lngMapping);
		
		MappingPropertyUnary timestampMapping = new MappingPropertyUnary(URI.create(e4.getElementId()), "mapping-timestamp", "Time Field", "Specifies a field that contains a timestamp value");
		timestampMapping.setValueRequired(true);
		staticProperties.add(timestampMapping);
		
		MappingPropertyUnary partitionMapping = new MappingPropertyUnary(URI.create(e3.getElementId()), "mapping-partition", "Partition Property", "Specifies a field that should be used to partition the stream (e.g., a vehicle plate number)");
		partitionMapping.setValueRequired(false);
		staticProperties.add(partitionMapping);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		
		int radius = (int) Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(invocationGraph, "radius"));
		
		DomainStaticProperty dsp = SepaUtils.getDomainStaticPropertyBy(invocationGraph, "location");
		double latitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(dsp, Geo.lat));
		double longitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(dsp, Geo.lng));
		
		GeofencingData geofencingData = new GeofencingData(latitude, longitude, radius);
		
		String latitudeMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-latitude");
		String longitudeMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-longitude");
		String partitionMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-partition");
		String timestampMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-timestamp");
		DurationOfStayParameters params = new DurationOfStayParameters(invocationGraph, geofencingData, latitudeMapping, longitudeMapping, partitionMapping, timestampMapping);

		return submit(params, DurationOfStay::new, invocationGraph);
	}

}

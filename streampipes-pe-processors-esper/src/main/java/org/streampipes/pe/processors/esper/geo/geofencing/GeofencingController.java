package org.streampipes.pe.processors.esper.geo.geofencing;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.Geo;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeofencingController extends StandaloneEventProcessorDeclarerSingleton<GeofencingParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		SpDataStream stream1 = new SpDataStream();
		EventSchema schema = new EventSchema();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e3 = EpRequirements.stringReq();
		schema.setEventProperties(Arrays.asList(e1, e2, e3));
		
		DataProcessorDescription desc = new DataProcessorDescription("geofencing", "Geofencing", "Detects whether a location-based stream moves inside a (circular) area around a given point described as latitude-longitude pair.");
		desc.setCategory(Arrays.asList(DataProcessorType.GEO.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema);
	
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> additionalProperties = new ArrayList<>();
		additionalProperties.add(EpProperties.longEp(Labels.empty(), "geofencingTime", "http://schema.org/DateTime"));
		additionalProperties.add(EpProperties.booleanEp(Labels.empty(), "insideGeofence", "http://schema.org/Text"));
		AppendOutputStrategy appendOutput = new AppendOutputStrategy();
		appendOutput.setEventProperties(additionalProperties);
		strategies.add(appendOutput);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Enter/Leave Area", "Specifies the operation that should be detected: A location-based stream can enter or leave the selected area.");
		operation.addOption(new Option("Enter"));
		operation.addOption(new Option("Leave"));
		
		staticProperties.add(operation);
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
		
		MappingPropertyUnary partitionMapping = new MappingPropertyUnary(URI.create(e3.getElementId()), "mapping-partition", "Partition Property", "Specifies a field that should be used to partition the stream (e.g., a vehicle plate number)");
		partitionMapping.setValueRequired(false);
		staticProperties.add(partitionMapping);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<GeofencingParameters> onInvocation
					(DataProcessorInvocation invocationGraph) {
		String operation = SepaUtils.getOneOfProperty(invocationGraph, "operation");

		int radius = (int) Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(invocationGraph, "radius"));

		DomainStaticProperty dsp = SepaUtils.getDomainStaticPropertyBy(invocationGraph, "location");
		double latitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(dsp, Geo.lat));
		double longitude = Double.parseDouble(SepaUtils.getSupportedPropertyValue(dsp, Geo.lng));

		GeofencingData geofencingData = new GeofencingData(latitude, longitude, radius);

		String latitudeMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-latitude");
		String longitudeMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-longitude");
		String partitionMapping = SepaUtils.getMappingPropertyName(invocationGraph, "mapping-partition");
		GeofencingParameters params = new GeofencingParameters(invocationGraph, getOperation(operation), geofencingData, latitudeMapping, longitudeMapping, partitionMapping);

		return new ConfiguredEventProcessor<>(params, Geofencing::new);
	}

	private GeofencingOperation getOperation(String operation) {
		if (operation.equals("Enter")) return GeofencingOperation.ENTER;
		else return GeofencingOperation.LEAVE;
	}

}

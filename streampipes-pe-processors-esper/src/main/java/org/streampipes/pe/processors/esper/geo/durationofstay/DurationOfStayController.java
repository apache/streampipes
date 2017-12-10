package org.streampipes.pe.processors.esper.geo.durationofstay;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.Geo;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.pe.processors.esper.geo.geofencing.GeofencingData;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DurationOfStayController extends StandaloneEventProcessorDeclarerSingleton<DurationOfStayParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		SpDataStream stream1 = new SpDataStream();
		EventSchema schema = new EventSchema();
		EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
		EventProperty e3 = new EventPropertyPrimitive();
		EventProperty e4 = EpRequirements.domainPropertyReq("http://schema.org/DateTime");
		
		schema.setEventProperties(Arrays.asList(e1, e2, e3, e4));
		
		DataProcessorDescription desc = new DataProcessorDescription("durationofstay", "Duration of Stay", "Calculates the duration of stay of a location-based object within a specified radius around a specified point-based coordinate.");
		desc.setCategory(Arrays.asList(DataProcessorType.GEO.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema);
	
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> additionalProperties = new ArrayList<>();
		additionalProperties.add(EpProperties.longEp(Labels.empty(), "departureTime", "http://schema.org/DateTime"));
		additionalProperties.add(EpProperties.longEp(Labels.empty(), "durationOfStay", "http://schema.org/Number"));
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
	public ConfiguredEventProcessor<DurationOfStayParameters, EventProcessor<DurationOfStayParameters>> onInvocation
					(DataProcessorInvocation invocationGraph) {
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

		return new ConfiguredEventProcessor<>(params, DurationOfStay::new);
	}

}

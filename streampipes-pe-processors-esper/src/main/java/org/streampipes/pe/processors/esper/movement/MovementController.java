package org.streampipes.pe.processors.esper.movement;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.container.util.StandardTransportFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.quality.Accuracy;
import org.streampipes.model.impl.quality.EventPropertyQualityRequirement;
import org.streampipes.model.impl.quality.EventStreamQualityRequirement;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.model.impl.staticproperty.MappingPropertyNary;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;


public class MovementController extends StandaloneEventProcessorDeclarerSingleton<MovementParameter> {

	private static final Logger logger = LoggerFactory
			.getLogger("MovementTest");

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("movement", "Movement Analysis",
				"Movement Analysis Enricher");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Movement_Analysis_Icon_1_HQ.png");
		desc.setCategory(Arrays.asList(EpaType.GEO.name()));
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			
			List<EventPropertyQualityRequirement> latitudeQualities = new ArrayList<EventPropertyQualityRequirement>();
			latitudeQualities.add(new EventPropertyQualityRequirement(new Accuracy(5), null));
			
			EventProperty e1 = EpRequirements.domainPropertyReq(Geo.lat);
			e1.setRequiresEventPropertyQualities(latitudeQualities);

			List<EventPropertyQualityRequirement> longitudeQualities = new ArrayList<EventPropertyQualityRequirement>();
			longitudeQualities.add(new EventPropertyQualityRequirement(new Accuracy(10), new Accuracy(30)));
	
			EventProperty e2 = EpRequirements.domainPropertyReq(Geo.lng);
			e2.setRequiresEventPropertyQualities(longitudeQualities);
			
			eventProperties.add(e1);
			eventProperties.add(e2);

			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri("http://localhost:8090/" + desc.getElementId());

			List<EventStreamQualityRequirement> eventStreamQualities = new ArrayList<EventStreamQualityRequirement>();
			Frequency maxFrequency = new Frequency((float) 0.5);
			eventStreamQualities.add(new EventStreamQualityRequirement(null, maxFrequency));
			stream1.setRequiresEventStreamQualities(eventStreamQualities);

			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"speed", "", Utils.createURI("http://schema.org/Number")));
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"bearing", "", Utils.createURI("http://test.de/bearing")));
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"distance", "", Utils.createURI("http://test.de/distance")));
			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			OneOfStaticProperty epsg = new OneOfStaticProperty("epsg", "Select Projection", "");
			epsg.addOption(new Option("EPSG:4326"));
			epsg.addOption(new Option("EPSG:4329"));
			staticProperties.add(epsg);
			//TODO mapping properties
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping", ""));
			staticProperties.add(new MappingPropertyUnary(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping", ""));
			staticProperties.add(new MappingPropertyNary("group by", "Group elements by", ""));
			desc.setStaticProperties(staticProperties);

						
		} catch (Exception e) {
			e.printStackTrace();
		}
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
					
		String epsgProperty = null;
		OneOfStaticProperty osp = ((OneOfStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "epsg")));
		for(Option option : osp.getOptions())
			if (option.isSelected()) epsgProperty = option.getName();
		
		String xProperty = SepaUtils.getMappingPropertyName(sepa,
				"latitude");
		String yProperty = SepaUtils.getMappingPropertyName(sepa,
				"longitude");

		MovementParameter staticParam = new MovementParameter(
				sepa,
				Arrays.asList("userName"), epsgProperty, "timestamp", xProperty,
				yProperty, 8000L); // TODO reduce param overhead

		return submit(staticParam, MovementAnalysis::new);

	}
}

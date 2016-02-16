package de.fzi.cep.sepa.esper.movement;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class MovementController extends FlatEpDeclarer<MovementParameter> {

	private static final Logger logger = LoggerFactory
			.getLogger("MovementTest");

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("sepa/movement", "Movement Analysis",
				"Movement Analysis Enricher");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Movement_Analysis_Icon_1_HQ.png");
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
					"speed", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"bearing", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/bearing")));
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"distance", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/distance")));
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

		try {
			invokeEPRuntime(staticParam, MovementAnalysis::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}

	}
}

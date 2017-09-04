package org.streampipes.pe.processors.esper.proasense.mhwirth.single;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MhWirth;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DrillingStopEnrichedController extends StandaloneEventProcessorDeclarerSingleton<DrillingStopEnrichedParameters> {

	@Override
	public ConfiguredEventProcessor<DrillingStopEnrichedParameters, EventProcessor<DrillingStopEnrichedParameters>>
	onInvocation(SepaInvocation sepa) {
		int minRpm = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa, "rpm"));
		int minTorque = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa, "torque"));

		String latPropertyName = SepaUtils.getMappingPropertyName(sepa, "rpm");
		String lngPropertyName = SepaUtils.getMappingPropertyName(sepa, "torque");

		System.out.println(minRpm +", " +minTorque +", " +latPropertyName +", " +lngPropertyName);
		DrillingStopEnrichedParameters staticParam = new DrillingStopEnrichedParameters(
						sepa,
						minRpm,
						minTorque,
						latPropertyName,
						lngPropertyName);

		return new ConfiguredEventProcessor<>(staticParam, DrillingStopEnriched::new);
	}

	@Override
	public SepaDescription declareModel() {
	
		EventStream stream1 = new EventStream();
		
		EventSchema schema1 = new EventSchema();
		EventPropertyPrimitive p1 = EpRequirements.domainPropertyReq(MhWirth.Rpm);
		schema1.addEventProperty(p1);
		
		EventPropertyPrimitive p2 = EpRequirements.domainPropertyReq(MhWirth.Torque);
		schema1.addEventProperty(p2);
		
		
		SepaDescription desc = new SepaDescription("drillingstopenriched", "Drilling Stop", "Detects stop of a drilling process (starting from single event source)");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Drilling_Stop_HQ.png");
		desc.setCategory(Arrays.asList(EpaType.ALGORITHM.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema1);
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
		
		EventProperty result = new EventPropertyPrimitive(XSD._boolean.toString(),
				"drillingStatus", "", Utils.createURI(MhWirth.DrillingStatus));;
	
		appendProperties.add(result);
		strategies.add(new AppendOutputStrategy(appendProperties));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty rpmThreshold = new FreeTextStaticProperty("rpm", "RPM threshold", "");
		FreeTextStaticProperty torqueThreshold = new FreeTextStaticProperty("torque", "Torque threshold", "");
		staticProperties.add(rpmThreshold);
		staticProperties.add(torqueThreshold);
		
		staticProperties.add(new MappingPropertyUnary(URI.create(p1.getElementName()), "rpm", "Select RPM Mapping", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(p2.getElementName()), "torque", "Select Torque Mapping", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}
	
}

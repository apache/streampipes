package org.streampipes.pe.processors.esper.absence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.CustomOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.wrapper.standalone.declarer.FlatEventProcessorDeclarer;
import org.streampipes.container.util.StandardTransportFormat;

public class AbsenceController extends FlatEventProcessorDeclarer<AbsenceParameters> {

	@Override
	public SepaDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventStream stream2 = new EventStream();
		
		SepaDescription desc = new SepaDescription("absence", "Absence", "Detects whether an event does not arrive within a specified time after the occurrence of another event.");
		desc.setCategory(Arrays.asList(EpaType.PATTERN_DETECT.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy(false));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
		staticProperties.add(StaticProperties.integerFreeTextProperty("timeWindow", "Time Window Size", "Time window size (seconds)"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}

		int timeWindowSize = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());
		
		AbsenceParameters staticParam = new AbsenceParameters(sepa, selectProperties, timeWindowSize);

		return submit(staticParam, Absence::new, sepa);
	}
}

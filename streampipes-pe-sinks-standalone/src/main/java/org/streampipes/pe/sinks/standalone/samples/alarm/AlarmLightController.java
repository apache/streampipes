package org.streampipes.pe.sinks.standalone.samples.alarm;

import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class AlarmLightController extends StandaloneEventSinkDeclarer<AlarmLightParameters> {

	@Override
	public SecDescription declareModel() {
		return DataSinkBuilder.create("alarm", "Alarm Light", "Switches the alarm light on or off.")
						.category(EcType.ACTUATOR)
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.requiredSingleValueSelection("state", "On/Off", "Specifies whether the alarm light should be turned " +
										"on or off.", Options.from("On", "Off"))
						.build();
	}

	@Override
	public ConfiguredEventSink<AlarmLightParameters, EventSink<AlarmLightParameters>> onInvocation(SecInvocation graph) {
		String selectedOption = getExtractor(graph).selectedSingleValue("state", String.class);
		AlarmLightParameters params = new AlarmLightParameters(graph, selectedOption);

		return new ConfiguredEventSink<>(params, AlarmLight::new);

	}

}

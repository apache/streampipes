package org.streampipes.pe.sinks.standalone.samples.demonstrator.ventil;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class DemonstratorVentilController extends StandaloneEventSinkDeclarer<DemonstratorVentilParameters> {

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("demonstrator_ventil", "Ventil of Demonstrator", "Turns on or " +
				"off the the ventil of the Demonstrator")
				.category(DataSinkType.ACTUATOR)
				.requiredPropertyStream1(EpRequirements.anyProperty())
				.supportedFormats(SupportedFormats.jsonFormat())
				.supportedProtocols(SupportedProtocols.kafka())
				.requiredSingleValueSelection("state", "On/Off", "Specifies whether the ventil should be turned " +
						"on or off.", Options.from("On", "Off"))
				.build();
	}

	@Override
	public ConfiguredEventSink<DemonstratorVentilParameters, EventSink<DemonstratorVentilParameters>> onInvocation(DataSinkInvocation graph) {
		String selectedOption = getExtractor(graph).selectedSingleValue("state", String.class);
		DemonstratorVentilParameters params = new DemonstratorVentilParameters(graph, selectedOption);

		return new ConfiguredEventSink<>(params, DemonstratorVentil::new);

	}

}

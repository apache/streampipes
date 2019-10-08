package org.streampipes.sinks.brokers.jvm.bufferrest;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

import java.util.List;

public class BufferRestController extends StandaloneEventSinkDeclarer<BufferRestParameters> {

	private static final String KEY = "bufferrest";
	private static final String URI = ".uri";
	private static final String COUNT = ".count";
	private static final String FIELDS = ".fields-to-send";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("org.streampipes.sinks.brokers.jvm.bufferrest")
						.category(DataSinkType.NOTIFICATION)
						.withLocales(Locales.EN)
						.requiredStream(StreamRequirementsBuilder
								.create()
								.requiredPropertyWithNaryMapping(EpRequirements.anyProperty(), Labels.withId(
										KEY + FIELDS), PropertyScope.NONE)
								.build())
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
 						.requiredTextParameter(Labels.from(KEY + URI, "REST Endpoint URI", "REST Endpoint URI"))
						.requiredIntegerParameter(Labels.from(KEY + COUNT, "Buffered Event Count",
								"Number (1 <= x <= 1000000) of incoming events before sending data on to the given REST endpoint"),
								1, 1000000, 1)
						.build();
	}

	@Override
	public ConfiguredEventSink<BufferRestParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

		List<String> fieldsToSend = extractor.mappingPropertyValues(KEY + FIELDS);
		String restEndpointURI = extractor.singleValueParameter(KEY + URI, String.class);
		int bufferSize = Integer.parseInt(extractor.singleValueParameter(KEY + COUNT, String.class));

		BufferRestParameters params = new BufferRestParameters(graph, fieldsToSend, restEndpointURI, bufferSize);

		return new ConfiguredEventSink<>(params, BufferRest::new);
	}
}

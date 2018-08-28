#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.sink.${packageName};

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class ${classNamePrefix}Controller extends StandaloneEventSinkDeclarer<${classNamePrefix}Parameters> {

	private static final String EXAMPLE_KEY = "example-key";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("${package}-${packageName}", "${classNamePrefix}", "Description")
						.category(DataSinkType.NOTIFICATION)
						.requiredStream(StreamRequirementsBuilder
							.create()
							.requiredProperty(EpRequirements.anyProperty())
							.build())
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.requiredTextParameter(Labels.from(EXAMPLE_KEY, "Example Text Parameter", "Example " +
				"Text Parameter Description"))
						.build();
	}

	@Override
	public ConfiguredEventSink<${classNamePrefix}Parameters> onInvocation(DataSinkInvocation graph) {

		String exampleString = extractor.singleValueParameter(EXAMPLE_KEY, String.class);

		${classNamePrefix}Parameters params = new ${classNamePrefix}Parameters(graph, exampleString);

		return new ConfiguredEventSink<>(params, () -> new ${classNamePrefix}(params));
	}

}

package org.streampipes.pe.mixed.flink.samples.rename;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.helpers.TransformOperations;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

public class FieldRenamerController extends FlinkDataProcessorDeclarer<FieldRenamerParameters> {

	private static final String CONVERT_PROPERTY = "convert-property";
	private static final String FIELD_NAME = "field-name";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("rename", "Field Renamer", "Replaces the runtime name of an event property with a custom defined name. Useful for data ingestion purposes where a specific event schema is needed.")
						.requiredStream(StreamRequirementsBuilder
										.create()
										.requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.from
														(CONVERT_PROPERTY,"Property", "The" +
																		" property to convert"), PropertyScope.NONE)
										.build())
						.requiredTextParameter(Labels.from(FIELD_NAME, "The new field name", ""))
						.supportedProtocols(SupportedProtocols.kafka())
						.supportedFormats(SupportedFormats.jsonFormat())
						.outputStrategy(OutputStrategies.transform(TransformOperations
										.dynamicRuntimeNameTransformation(CONVERT_PROPERTY, FIELD_NAME)))
						.build();
	}

	@Override
	public FlinkDataProcessorRuntime<FieldRenamerParameters> getRuntime(
					DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
		String oldPropertyName = extractor.mappingPropertyValue(CONVERT_PROPERTY);
		String newPropertyName = extractor.singleValueParameter(FIELD_NAME, String.class);
		
		return new FieldRenamerProgram (
				new FieldRenamerParameters(graph, oldPropertyName, newPropertyName),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
						FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
	}

}

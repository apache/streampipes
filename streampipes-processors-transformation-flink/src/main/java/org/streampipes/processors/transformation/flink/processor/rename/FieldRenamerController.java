/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.flink.processor.rename;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.flink.config.TransformationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class FieldRenamerController extends FlinkDataProcessorDeclarer<FieldRenamerParameters> {

	private static final String CONVERT_PROPERTY = "convert-property";
	private static final String FIELD_NAME = "field-name";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("org.streampipes.processors.transformation.flink.field-renamer", "Field Renamer", "Replaces the runtime name of an event property with a custom defined name. Useful for data ingestion purposes where a specific event schema is needed.")
						.iconUrl(TransformationFlinkConfig.getIconUrl("field_renamer"))
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
				new FieldRenamerParameters(graph, oldPropertyName, newPropertyName));
	}

}

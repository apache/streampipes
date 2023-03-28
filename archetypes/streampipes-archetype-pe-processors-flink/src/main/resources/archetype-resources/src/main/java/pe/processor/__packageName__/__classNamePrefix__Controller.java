/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class ${classNamePrefix}Controller extends FlinkDataProcessorDeclarer<${classNamePrefix}Parameters> {

	private static final String EXAMPLE_KEY = "example-key";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("${package}.pe.processor.${packageName}")
        		.withAssets(Assets.DOCUMENTATION, Assets.ICON)
						.withLocales(Locales.EN)
						.category(DataProcessorType.ENRICH)
						.requiredStream(StreamRequirementsBuilder
							.create()
							.requiredProperty(EpRequirements.anyProperty())
							.build())
						.requiredTextParameter(Labels.withId(EXAMPLE_KEY))
						.outputStrategy(OutputStrategies.append(
								PrimitivePropertyBuilder.create(
										Datatypes.String, "appendedText")
								.build()))
						.build();
	}

	@Override
	public FlinkDataProcessorRuntime<${classNamePrefix}Parameters> getRuntime(
			DataProcessorInvocation graph,
			ProcessingElementParameterExtractor extractor,
			ConfigExtractor configExtractor,
			StreamPipesClient streamPipesClient) {

		String exampleText = extractor.singleValueParameter(EXAMPLE_KEY, String.class);
		${classNamePrefix}Parameters params = new ${classNamePrefix}Parameters(graph, exampleText);

		return new ${classNamePrefix}Program(params, configExtractor, streamPipesClient);
	}
}

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
package ${package}.pe.sink.${packageName};

import ${package}.config.Config;
import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;

public class ${classNamePrefix}Controller extends FlinkDataSinkDeclarer<${classNamePrefix}Parameters> {

	private static final String HOST_KEY = "host-key";
	private static final String PORT_KEY = "port-key";
	private static final String PASSWORD_KEY = "password-key";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("${package}.pe.sink.${packageName}")
						.category(DataSinkType.NOTIFICATION)
						.withAssets(Assets.DOCUMENTATION, Assets.ICON)
						.withLocales(Locales.EN)
						.requiredStream(StreamRequirementsBuilder
							.create()
							.requiredProperty(EpRequirements.anyProperty())
							.build())
						.requiredTextParameter(Labels.withId(HOST_KEY))
						.requiredIntegerParameter(Labels.withId(PORT_KEY), 1234)
						.requiredSecret(Labels.withId(PASSWORD_KEY))
						.build();
	}

	@Override
	public FlinkDataSinkRuntime<${classNamePrefix}Parameters> getRuntime(DataSinkInvocation graph,
																																			DataSinkParameterExtractor extractor,
																																			ConfigExtractor configExtractor,
																																			StreamPipesClient streamPipesClient) {

		String host = extractor.singleValueParameter(HOST_KEY, String.class);
		int port = extractor.singleValueParameter(PORT_KEY, Integer.class);
		String password = extractor.secretValue(PASSWORD_KEY);
		${classNamePrefix}Parameters params = new ${classNamePrefix}Parameters(graph, host, port, password);

		return new ${classNamePrefix}Program(params, configExtractor, streamPipesClient);
	}
}

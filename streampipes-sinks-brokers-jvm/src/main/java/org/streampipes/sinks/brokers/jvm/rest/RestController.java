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

package org.streampipes.sinks.brokers.jvm.rest;


import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class RestController extends StandaloneEventSinkDeclarer<RestParameters> {

	private static final String URL_KEY = "url_key";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("org.streampipes.sinks.brokers.jvm.rest", "REST Publisher", "Posts events to a REST interface")
						.requiredStream(StreamRequirementsBuilder
										.create()
										.requiredProperty(EpRequirements.anyProperty())
										.build())
						.requiredTextParameter(Labels.from(URL_KEY, "REST URL", "URL of the REST endoint"), false, false)
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.build();
	}

	@Override
	public ConfiguredEventSink<RestParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

		String url = extractor.singleValueParameter(URL_KEY, String.class);

		RestParameters params = new RestParameters(graph, url);

		return new ConfiguredEventSink<>(params, () -> new RestPublisher(params));
	}
}

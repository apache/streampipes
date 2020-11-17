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

package org.apache.streampipes.sinks.brokers.jvm.bufferrest;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

import java.util.List;

public class BufferRestController extends StandaloneEventSinkDeclarer<BufferRestParameters> {

	private static final String KEY = "bufferrest";
	private static final String URI = ".uri";
	private static final String COUNT = ".count";
	private static final String FIELDS = ".fields-to-send";

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.bufferrest")
						.category(DataSinkType.NOTIFICATION)
						.withLocales(Locales.EN)
						.requiredStream(StreamRequirementsBuilder
								.create()
								.requiredPropertyWithNaryMapping(EpRequirements.anyProperty(), Labels.withId(
										KEY + FIELDS), PropertyScope.NONE)
								.build())
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

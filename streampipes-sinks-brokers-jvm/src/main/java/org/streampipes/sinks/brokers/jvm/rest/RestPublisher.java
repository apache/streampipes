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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.logging.api.Logger;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;
import java.util.Map;

public class RestPublisher extends EventSink<RestParameters> {
	private static Logger logger;

	private String url;
	private JsonDataFormatDefinition jsonDataFormatDefinition;

	public RestPublisher(RestParameters params) {
		super(params);
		this.url = params.getUrl();
	}

	@Override
	public void bind(RestParameters params) throws SpRuntimeException {
		logger = params.getGraph().getLogger(RestPublisher.class);
		jsonDataFormatDefinition = new JsonDataFormatDefinition();
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {

		byte[] json = null;
		try {
			json = jsonDataFormatDefinition.fromMap(event);
		} catch (SpRuntimeException e) {
			logger.error("Error while serializing event: " + event + " Exception: " + e);
		}

		try {
			Request.Post(url)
					.bodyByteArray(json, ContentType.APPLICATION_JSON)
					.connectTimeout(1000)
					.socketTimeout(100000)
					.execute().returnContent().asString();
		} catch (IOException e) {
			logger.error("Error while sending data to endpoint: " + url + " Exception: " + e);
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
	}
}

/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sinks.brokers.jvm.kafka;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class KafkaPublisher extends EventSink<KafkaParameters> {

	private SpKafkaProducer producer;
	private JsonDataFormatDefinition dataFormatDefinition;

	public KafkaPublisher(KafkaParameters params) {
		super(params);
		this.dataFormatDefinition = new JsonDataFormatDefinition();
	}

	@Override
	public void bind(KafkaParameters parameters) throws SpRuntimeException {
		this.producer = new SpKafkaProducer(parameters.getKafkaHost() +":" +parameters.getKafkaPort(), parameters
						.getTopic());
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		try {
			producer.publish(dataFormatDefinition.fromMap(event));
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.producer.disconnect();
	}
}

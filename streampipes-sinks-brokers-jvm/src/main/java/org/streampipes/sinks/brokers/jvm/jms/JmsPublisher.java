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

package org.streampipes.sinks.brokers.jvm.jms;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class JmsPublisher extends EventSink<JmsParameters> {

	private ActiveMQPublisher publisher;
	private JsonDataFormatDefinition jsonDataFormatDefinition;
	
	public JmsPublisher(JmsParameters params) {
		super(params);
		this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
	}

	@Override
	public void bind(JmsParameters params) throws SpRuntimeException {
		this.publisher = new ActiveMQPublisher(params.getJmsHost() +":" +params.getJmsPort(), params.getTopic());
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		try {
			this.publisher.publish(jsonDataFormatDefinition.fromMap(event));
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.publisher.disconnect();
	}
}

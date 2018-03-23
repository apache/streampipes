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

package org.streampipes.wrapper.flink.sink;

import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.streampipes.model.grounding.JmsTransportProtocol;


public class FlinkJmsProducer<IN> extends RichSinkFunction<IN>  { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JmsTransportProtocol protocol;
	
	private SerializationSchema<IN> serializationSchema;
	
	private ActiveMQPublisher publisher;
	
	public FlinkJmsProducer(JmsTransportProtocol protocol, SerializationSchema<IN>
					serializationSchema) {
		this.protocol = protocol;
		this.serializationSchema = serializationSchema;
	}
	
	@Override
	public void open(Configuration configuration) throws Exception {
		try {
			publisher = new ActiveMQPublisher();
			publisher.connect(protocol);
		} catch (Exception e)
		{
			throw new Exception("Failed to open Jms connection: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void invoke(IN value) throws Exception {
		byte[] msg = serializationSchema.serialize(value);
		publisher.publish(msg);
	}
}



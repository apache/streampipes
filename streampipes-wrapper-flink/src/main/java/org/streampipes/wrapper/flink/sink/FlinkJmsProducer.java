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



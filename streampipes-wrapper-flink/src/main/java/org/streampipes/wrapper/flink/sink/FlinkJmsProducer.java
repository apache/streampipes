package org.streampipes.wrapper.flink.sink;

import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.util.serialization.SerializationSchema;


public class FlinkJmsProducer<IN> extends RichSinkFunction<IN>  { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String brokerUrl;
	private String producerTopic;
	
	private SerializationSchema<IN> serializationSchema;
	
	private ActiveMQPublisher publisher;
	
	public FlinkJmsProducer(String brokerUrl, String producerTopic, SerializationSchema<IN> serializationSchema) {
		this.brokerUrl = brokerUrl;
		this.producerTopic = producerTopic;
		this.serializationSchema = serializationSchema;
	}
	
	@Override
	public void open(Configuration configuration) throws Exception {
		try {
			publisher = new ActiveMQPublisher(brokerUrl, producerTopic);
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



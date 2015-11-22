package de.fzi.cep.sepa.flink;

import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.api.KafkaSink;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import de.fzi.cep.sepa.flink.serializer.SimpleJmsSerializer;
import de.fzi.cep.sepa.flink.serializer.SimpleKafkaSerializer;
import de.fzi.cep.sepa.flink.sink.FlinkJmsProducer;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public abstract class FlinkSepaRuntime<B extends BindingParameters> extends FlinkRuntime<SepaInvocation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected B params;
	
	
	public FlinkSepaRuntime(B params)
	{
		super(params.getGraph());
		this.params = params;
	}
	
	public FlinkSepaRuntime(B params, FlinkDeploymentConfig config)
	{
		super(params.getGraph(), config);
		this.params = params;
		}
	
	@SuppressWarnings("deprecation")
	public boolean execute(DataStream<Map<String, Object>> convertedStream)
	{
		DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);
		
		SerializationSchema<Map<String, Object>, byte[]> kafkaSerializer = new SimpleKafkaSerializer();
		SerializationSchema<Map<String, Object>, String> jmsSerializer = new SimpleJmsSerializer();
		//applicationLogic.print();
		if (isOutputKafkaProtocol()) applicationLogic.addSink(new KafkaSink<Map<String, Object>>(getProperties().getProperty("bootstrap.servers"), getOutputTopic(), kafkaSerializer));
		else applicationLogic.addSink(new FlinkJmsProducer<>(getJmsBrokerAddress(), getOutputTopic(), jmsSerializer));
		
		thread = new Thread(this);
		thread.start();
				
		return true;
	}
	
	
	protected abstract DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>> messageStream);
		
	private String getOutputTopic()
	{
		return params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol().getTopicName();
	}
	
	private String getJmsBrokerAddress()
	{
		return ((JmsTransportProtocol) params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol()).getBrokerHostname()
				+":"
				+((JmsTransportProtocol)params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol()).getPort();
	}
		
	private boolean isOutputKafkaProtocol()
	{
		return params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol;
	}
}

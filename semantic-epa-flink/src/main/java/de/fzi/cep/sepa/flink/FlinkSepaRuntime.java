package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.flink.serializer.SimpleJmsSerializer;
import de.fzi.cep.sepa.flink.serializer.SimpleKafkaSerializer;
import de.fzi.cep.sepa.flink.sink.FlinkJmsProducer;
import de.fzi.cep.sepa.flink.sink.NonParallelKafkaProducer;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import java.util.Map;
import java.util.Properties;

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
		
		SerializationSchema<Map<String, Object>> kafkaSerializer = new SimpleKafkaSerializer();
		SerializationSchema<Map<String, Object>> jmsSerializer = new SimpleJmsSerializer();
		//applicationLogic.print();
		if (isOutputKafkaProtocol()) applicationLogic
			.addSink(new NonParallelKafkaProducer<>(getKafkaUrl(), getOutputTopic(), kafkaSerializer));
		else applicationLogic
			.addSink(new FlinkJmsProducer<>(getJmsBrokerAddress(), getOutputTopic(), jmsSerializer));
		
		thread = new Thread(this);
		thread.start();
				
		return true;
	}
	
	
	protected abstract DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>> messageStream);
		
	private String getOutputTopic()
	{
		return protocol()
				.getTopicName();
	}
	
	private String getJmsBrokerAddress()
	{
		return ((JmsTransportProtocol) protocol())
				.getBrokerHostname()
				+":"
				+((JmsTransportProtocol) protocol())
						.getPort();
	}
		
	private boolean isOutputKafkaProtocol()
	{
		return protocol() instanceof KafkaTransportProtocol;
	}
	
	private TransportProtocol protocol() {
		return params
				.getGraph()
				.getOutputStream()
				.getEventGrounding()
				.getTransportProtocol();
	}
	
	private Properties getProducerProperties() {
		Properties properties = new Properties();
		properties.put("client.id", graph.getCorrespondingPipeline()+"-" +getOutputTopic());
		properties.put("metadata.broker.list", getProperties().get("bootstrap.servers"));
		properties.put("bootstrap.servers", getProperties().get("bootstrap.servers"));
		return properties;
	}

	private String getKafkaUrl() {
		return String.valueOf(getProperties().get("bootstrap.servers"));
	}
	
}

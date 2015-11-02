package de.fzi.cep.sepa.flink;

import java.io.Serializable;
import java.util.Properties;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;
import org.apache.flink.streaming.connectors.kafka.api.KafkaSink;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public abstract class FlinkSepaRuntime<B extends BindingParameters, T> implements Serializable {

	private B params;
	private FlinkDeploymentConfig config;
	
	private boolean debug;
	
	
	private StreamExecutionEnvironment env;
	
	
	public FlinkSepaRuntime(B params)
	{
		this.params = params;
		this.debug = true;
	}
	
	public FlinkSepaRuntime(B params, FlinkDeploymentConfig config)
	{
		this.params = params;
		this.config = config;
		this.debug = false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean execute()
	{
		if (debug) this.env = StreamExecutionEnvironment.createLocalEnvironment();
		else this.env = StreamExecutionEnvironment.createRemoteEnvironment(config.getHost(), config.getPort(), config.getJarFile());
			
		DataStream<String> messageStream = env
				  .addSource(new FlinkKafkaConsumer082<>(getInputTopic(), new SimpleStringSchema(), getProperties()));
		
		DataStream<T> applicationLogic = getApplicationLogic(messageStream);
		
		SerializationSchema<T, byte[]> schema = getSerializer();
		
		applicationLogic.addSink(new KafkaSink<T>(getProperties().getProperty("bootstrap.servers"), getOutputTopic(), schema));
		
		try {
			env.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean stop()
	{
		return true;
	}
	
	protected abstract SerializationSchema<T, byte[]> getSerializer();
	
	protected abstract DataStream<T> getApplicationLogic(DataStream<String> messageStream);
	
	
	private String getInputTopic()
	{
		return ((KafkaTransportProtocol) params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getTopicName();
	}
	
	private String getOutputTopic()
	{
		return ((KafkaTransportProtocol) params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol()).getTopicName();
	}
	
	private Properties getProperties() {
		
		String zookeeperHost = ((KafkaTransportProtocol) params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getZookeeperHost();
		int zookeeperPort = ((KafkaTransportProtocol) params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getZookeeperPort();

		String kafkaHost = ((KafkaTransportProtocol) params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getBrokerHostname();
		int kafkaPort = ((KafkaTransportProtocol) params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getKafkaPort();

		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperHost +":" +zookeeperPort);
		props.put("bootstrap.servers", kafkaHost +":" +kafkaPort);
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");
		return props;
	}
}

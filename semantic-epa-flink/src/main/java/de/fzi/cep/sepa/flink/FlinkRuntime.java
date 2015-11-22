package de.fzi.cep.sepa.flink;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import de.fzi.cep.sepa.flink.converter.JsonToMapFormat;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public abstract class FlinkRuntime<I extends InvocableSEPAElement> implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean debug;
	
	protected Thread thread;
	
	protected StreamExecutionEnvironment env;
	protected FlinkDeploymentConfig config;	
	
	protected I graph;
	
	public FlinkRuntime(I graph) {
		this.graph = graph;
		this.config = new FlinkDeploymentConfig("", "localhost", 6123);
		this.debug = true;
	}
	
	public FlinkRuntime(I graph, FlinkDeploymentConfig config) {
		this.config = config;
		this.debug = false;
	}
	
	public boolean startExecution() {
		
		if (debug) this.env = StreamExecutionEnvironment.createLocalEnvironment();
		else this.env = StreamExecutionEnvironment
				.createRemoteEnvironment(config.getHost(), config.getPort(), config.getJarFile());
			
		DataStream<String> messageStream = env
				  .addSource(new FlinkKafkaConsumer082<>(getInputTopic(), new SimpleStringSchema(), getProperties()));
		
		DataStream<Map<String, Object>> convertedStream = messageStream.flatMap(new JsonToMapFormat());
	
		return execute(convertedStream);
	}
	
	public abstract boolean execute(DataStream<Map<String, Object>> convertedStream);
	
	
	public void run()
	{
		try {
			env.execute(graph.getElementId());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean stop()
	{
		FlinkJobController ctrl = new FlinkJobController(config.getHost(), config.getPort());
		try {
			return ctrl.deleteJob(ctrl.findJobId(ctrl.getJobManagerGateway(), graph.getElementId()));	
	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	protected String getInputTopic()
	{
		return protocol().getTopicName();
	}
	
	protected Properties getProperties() {
		
		String zookeeperHost = ((KafkaTransportProtocol) protocol()).getZookeeperHost();
		int zookeeperPort = ((KafkaTransportProtocol) protocol()).getZookeeperPort();

		String kafkaHost = ((KafkaTransportProtocol) protocol()).getBrokerHostname();
		int kafkaPort = ((KafkaTransportProtocol) protocol()).getKafkaPort();

		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperHost +":" +zookeeperPort);
		props.put("bootstrap.servers", kafkaHost +":" +kafkaPort);
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");
		return props;
	}
	
	private TransportProtocol protocol() {
		return graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
	}
}

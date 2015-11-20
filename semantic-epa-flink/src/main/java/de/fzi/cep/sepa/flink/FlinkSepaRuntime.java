package de.fzi.cep.sepa.flink;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;
import org.apache.flink.streaming.connectors.kafka.api.KafkaSink;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import de.fzi.cep.sepa.flink.converter.JsonToMapFormat;
import de.fzi.cep.sepa.flink.serializer.SimpleJmsSerializer;
import de.fzi.cep.sepa.flink.serializer.SimpleKafkaSerializer;
import de.fzi.cep.sepa.flink.sink.FlinkJmsProducer;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public abstract class FlinkSepaRuntime<B extends BindingParameters> implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected B params;
	private FlinkDeploymentConfig config;
	
	private boolean debug;
	
	private Thread thread;
	
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
		
		DataStream<Map<String, Object>> convertedStream = messageStream.flatMap(new JsonToMapFormat());
		DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);
		
		SerializationSchema<Map<String, Object>, byte[]> kafkaSerializer = new SimpleKafkaSerializer();
		SerializationSchema<Map<String, Object>, String> jmsSerializer = new SimpleJmsSerializer();
		
		if (isOutputKafkaProtocol()) applicationLogic.addSink(new KafkaSink<Map<String, Object>>(getProperties().getProperty("bootstrap.servers"), getOutputTopic(), kafkaSerializer));
		else applicationLogic.addSink(new FlinkJmsProducer<>(getJmsBrokerAddress(), getOutputTopic(), jmsSerializer));
		
		thread = new Thread(this);
		thread.start();
		
		return true;
	}
	
	public void run()
	{
		try {
			//JobExecutionResult result = env.execute();
			env.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean stop()
	{
		//ActorRef jobManager = JobManager.getJobManagerRemoteReference(new InetSocketAddress(config.getHost(), config.getPort()), AkkaUtils.createActorSystem(null), 1000);
		//Future<Object> response = Patterns.ask(jobManager, new CancelJob(jobId), 
		 
		thread.stop();
		return true;
	}
	
	protected abstract DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>> messageStream);
	
	
	private String getInputTopic()
	{
		return params.getGraph().getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
	}
	
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
	
	private boolean isOutputKafkaProtocol()
	{
		return params.getGraph().getOutputStream().getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol;
	}
}

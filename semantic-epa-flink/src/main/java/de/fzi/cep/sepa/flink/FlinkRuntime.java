package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.flink.converter.JsonToMapFormat;
import de.fzi.cep.sepa.flink.source.NonParallelKafkaSource;
import de.fzi.cep.sepa.flink.status.PipelineElementStatusSender;
import de.fzi.cep.sepa.flink.status.PipelineElementStatusSenderFactory;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class FlinkRuntime<I extends InvocableSEPAElement> implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean debug;
	
	protected Thread thread;
	
	protected StreamExecutionEnvironment env;
	protected FlinkDeploymentConfig config;

	private JobExecutionResult result;

	private PipelineElementStatusSender statusSender;
	
	protected I graph;
	
	public FlinkRuntime(I graph) {
		this(graph, new FlinkDeploymentConfig("", "localhost", 6123), true);
	}
	
	public FlinkRuntime(I graph, FlinkDeploymentConfig config) {
		this(graph, config, false);
	}

	private FlinkRuntime(I graph, FlinkDeploymentConfig config, boolean debug) {
		this.graph = graph;
		this.config = config;
		this.debug = debug;
		this.statusSender = PipelineElementStatusSenderFactory.getStatusSender(graph);
	}
	
	public boolean startExecution() {
		try {
			if (debug) this.env = StreamExecutionEnvironment.createLocalEnvironment();
			else this.env = StreamExecutionEnvironment
					.createRemoteEnvironment(config.getHost(), config.getPort(), config.getJarFile());

			//this.env = StreamExecutionEnvironment.getExecutionEnvironment();
			DataStream<String> messageStream = env
					//.addSource(new FlinkKafkaConsumer09<>(getInputTopic(), new SimpleStringSchema(), getProperties()));
			.addSource(new NonParallelKafkaSource(getKafkaHost() + ":" +getKafkaPort(), getInputTopic()));

			DataStream<Map<String, Object>> convertedStream = messageStream.flatMap(new JsonToMapFormat());

			execute(convertedStream);

			// TODO find a better solution
			// The loop waits until the job is deployed
			// When the deployment takes longer then 60 seconds it returns false
			FlinkJobController ctrl = new FlinkJobController(config.getHost(), config.getPort());
			boolean isDeployed = false;
			int count = 0;
			do {
				try {
					count++;
					Thread.sleep(1000);
					JobID l = ctrl.findJobId(ctrl.getJobManagerGateway(), graph.getElementId());
					isDeployed = true;

				} catch (Exception e) {

				}
			} while (!isDeployed && count < 60);

			if (count == 60) {
				return false;
			} else {
				return true;
			}

		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public abstract boolean execute(DataStream<Map<String, Object>> convertedStream);
	
	
	public void run()
	{
		try {
			result = env.execute(graph.getElementId());
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
		
		String zookeeperHost = getZookeeperHost();
		int zookeeperPort = getZookeeperPort();

		String kafkaHost = getKafkaHost();
		int kafkaPort = getKafkaPort();

		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperHost +":" +zookeeperPort);
		props.put("bootstrap.servers", kafkaHost +":" +kafkaPort);
		props.put("group.id", UUID.randomUUID().toString());
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");
		return props;
	}

	private String getKafkaHost() {
		return ((KafkaTransportProtocol) protocol()).getBrokerHostname();
	}

	private Integer getKafkaPort() {
		return ((KafkaTransportProtocol) protocol()).getKafkaPort();
	}

	private String getZookeeperHost() {
		return ((KafkaTransportProtocol) protocol()).getZookeeperHost();
	}

	private Integer getZookeeperPort() {
		return ((KafkaTransportProtocol) protocol()).getZookeeperPort();
	}
	
	private TransportProtocol protocol() {
		return graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
	}

	protected PipelineElementStatusSender getStatusSender() {
		return this.statusSender;
	}
}

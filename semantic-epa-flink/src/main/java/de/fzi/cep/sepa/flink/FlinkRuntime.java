package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.flink.converter.JsonToMapFormat;
import de.fzi.cep.sepa.flink.source.NonParallelKafkaSource;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

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
	}

	public boolean startExecution() {
		try {
			if (debug) this.env = StreamExecutionEnvironment.createLocalEnvironment();
			else this.env = StreamExecutionEnvironment
					.createRemoteEnvironment(config.getHost(), config.getPort(), config.getJarFile());

			List<DataStream<Map<String, Object>>> messageStreams = new ArrayList<>();

			// Add the first source to the topology
			DataStream<Map<String, Object>> messageStream1 = null;
 			SourceFunction<String> source1 = getStream1Source();
 			if (source1 != null) {
 			    messageStream1 = env
					.addSource(source1).flatMap(new JsonToMapFormat());
			} else {
				throw new Exception("At least one source must be defined for a flink sepa");
			}

			DataStream<Map<String, Object>> messageStream2 = null;
 			SourceFunction<String> source2 = getStream2Source();
 			if (source2 != null) {
 			    messageStream2 = env
					.addSource(source2).flatMap(new JsonToMapFormat());

				execute(messageStream1, messageStream2);
			} else {
				execute(messageStream1);
			}

			// TODO find a better solution
			// The loop waits until the job is deployed
			// When the deployment takes longer then 60 seconds it returns false
			// This check is not needed when the execution environment is st to local
			if (!debug) {
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
			} else {
				return true;
			}


		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public abstract boolean execute(DataStream<Map<String, Object>>... convertedStream);


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

//	protected String getInputTopic()
//	{
//		return protocol().getTopicName();
//	}

	protected Properties getProperties() {
		Properties props = new Properties();

	    // TODO ask Domink ig those properties are still needed or if they were for the old kafka producer
//		String zookeeperHost = getZookeeperHost();
//		int zookeeperPort = getZookeeperPort();
//
//		String kafkaHost = getKafkaHost();
//		int kafkaPort = getKafkaPort();

//		props.put("zookeeper.connect", zookeeperHost +":" +zookeeperPort);
//		props.put("bootstrap.servers", kafkaHost +":" +kafkaPort);
		props.put("group.id", UUID.randomUUID().toString());
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");
		return props;
	}

//	private String getKafkaHost() {
//		return ((KafkaTransportProtocol) protocol()).getBrokerHostname();
//	}
//
//	private Integer getKafkaPort() {
//		return ((KafkaTransportProtocol) protocol()).getKafkaPort();
//	}
//
//	private String getZookeeperHost() {
//		return ((KafkaTransportProtocol) protocol()).getZookeeperHost();
//	}
//
//	private Integer getZookeeperPort() {
//		return ((KafkaTransportProtocol) protocol()).getZookeeperPort();
//	}
//
//	private KafkaTransportProtocol protocol1() {
//		return (KafkaTransportProtocol) graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
//	}
//
//	private KafkaTransportProtocol protocol2() {
//		return (KafkaTransportProtocol) graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
//	}

	private SourceFunction<String> getStream1Source() {
		return getStreamSource(0);
	}

	private SourceFunction<String> getStream2Source() {
		return getStreamSource(1);
	}

	/**
	 * This method takes the i's input stream and creates a source for the flink graph
	 * Currently just kafka is supported as a protocol
	 * TODO Add also jms support
	 * @param i
	 * @return
	 */
	private SourceFunction<String> getStreamSource(int i) {
		EventStream stream = graph.getInputStreams().get(i);
		if (stream != null) {
			KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();


			//new FlinkKafkaConsumer09<>(getInputTopic(), new SimpleStringSchema(), getProperties());
			return new NonParallelKafkaSource(protocol.getBrokerHostname() + ":" + protocol.getKafkaPort(),
					protocol.getTopicName());
		} else {
			return null;
		}
	}
}

package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.flink.serializer.SimpleJmsSerializer;
import org.streampipes.wrapper.flink.serializer.SimpleKafkaSerializer;
import org.streampipes.wrapper.flink.sink.FlinkJmsProducer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.Map;

public abstract class FlinkDataProcessorRuntime<B extends EventProcessorBindingParams> extends FlinkRuntime<B,
				DataProcessorInvocation> {

	private static final long serialVersionUID = 1L;

	public FlinkDataProcessorRuntime(B params)
	{
		super(params);
	}

	public FlinkDataProcessorRuntime(B params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}

	@SuppressWarnings("deprecation")
	public void appendExecutionConfig(DataStream<Map<String, Object>>... convertedStream)
	{
		DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);

		SerializationSchema<Map<String, Object>> kafkaSerializer = new SimpleKafkaSerializer();
		SerializationSchema<Map<String, Object>> jmsSerializer = new SimpleJmsSerializer();
		if (isKafkaProtocol(getOutputStream())) applicationLogic
				.addSink(new FlinkKafkaProducer010<>(getKafkaUrl(getOutputStream()), getTopic(getOutputStream()), kafkaSerializer));
		else applicationLogic
				.addSink(new FlinkJmsProducer<>(getJmsProtocol(getOutputStream()), jmsSerializer));

	}

	private SpDataStream getOutputStream() {
		return getGraph().getOutputStream();
	}

	protected abstract DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream);


}

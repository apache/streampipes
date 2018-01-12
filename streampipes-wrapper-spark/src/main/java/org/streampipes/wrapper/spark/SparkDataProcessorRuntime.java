package org.streampipes.wrapper.spark;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.spark.serializer.SimpleKafkaSerializer;

import java.util.Map;

/**
 * Created by Jochen Lutz on 2017-12-07.
 */
public abstract class SparkDataProcessorRuntime<B extends EventProcessorBindingParams> extends SparkRuntime<DataProcessorInvocation> {
    private static final long serialVersionUID = 1L;
    protected B params;

    public SparkDataProcessorRuntime(B params) {
        super(params.getGraph());
        this.params = params;
    }

    protected abstract JavaDStream<Map<String, Object>> getApplicationLogic(JavaDStream<Map<String, Object>>... messageStream);

    @Override
    public boolean execute(JavaDStream<Map<String, Object>>... convertedStream) {
        /*
        DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);

		SerializationSchema<Map<String, Object>> kafkaSerializer = new SimpleKafkaSerializer();
		SerializationSchema<Map<String, Object>> jmsSerializer = new SimpleJmsSerializer();
		//applicationLogic.print();
		if (isOutputKafkaProtocol()) applicationLogic
				.addSink(new FlinkKafkaProducer010<>(getKafkaUrl(), getOutputTopic(), kafkaSerializer));
		else applicationLogic
				.addSink(new FlinkJmsProducer<>(getJmsProtocol(), jmsSerializer));


         */
        //TODO: aus convertedStream und OutputSerializer und Sink was bauen

        JavaDStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);
        //applicationLogic.print();

        if (isOutputKafkaProtocol()) {
            applicationLogic.foreachRDD(new SimpleKafkaSerializer(kafkaParams, protocol().getTopicName()));
        }
        else {
            //TODO: JMS
        }

        thread = new Thread(this);
        thread.start();

        return true;
    }

    private boolean isOutputKafkaProtocol() {
        return protocol() instanceof KafkaTransportProtocol;
    }

    private TransportProtocol protocol() {
        return params
                .getGraph()
                .getOutputStream()
                .getEventGrounding()
                .getTransportProtocol();
    }
}

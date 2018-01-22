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

    public SparkDataProcessorRuntime(B params, SparkDeploymentConfig deploymentConfig) {
        super(params.getGraph(), deploymentConfig);
        this.params = params;
    }

    protected abstract JavaDStream<Map<String, Object>> getApplicationLogic(JavaDStream<Map<String, Object>>... messageStream);

    @Override
    public boolean execute(JavaDStream<Map<String, Object>>... convertedStream) {
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
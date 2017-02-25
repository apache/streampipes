package de.fzi.cep.sepa.flink.samples.delay;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

public class DelayProgram extends FlinkSepaRuntime<DelayParameters> implements Serializable {



    public DelayProgram(DelayParameters params) {
        super(params);

    }

    public DelayProgram(DelayParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {
        DataStream<Map<String, Object>> dataStream = messageStream[0];
//
        dataStream
                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
                    @Override
                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of(0, value);
                    }
                })
                .keyBy(0)
                .flatMap(new SendToKafkaFlatMap(this.params));

        return dataStream;
    }


}

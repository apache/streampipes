package de.fzi.cep.sepa.flink.samples.delay.taxi;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

public class DelayTaxiProgram extends FlinkSepaRuntime<DelayTaxiParameters> implements Serializable {



    public DelayTaxiProgram(DelayTaxiParameters params) {
        super(params);

    }

    public DelayTaxiProgram(DelayTaxiParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {
        DataStream<Map<String, Object>> dataStream = messageStream[0];
//
        DataStream<Map<String, Object>> result = dataStream
                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
                    @Override
                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of(0, value);
                    }
                })
                .keyBy(0)
                .flatMap(new DelayTaxi(this.params));

        return result;
    }


}

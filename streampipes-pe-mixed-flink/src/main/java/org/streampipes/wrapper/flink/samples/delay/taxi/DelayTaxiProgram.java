package org.streampipes.wrapper.flink.samples.delay.taxi;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

public class DelayTaxiProgram extends FlinkSepaRuntime<DelayTaxiParameters> implements Serializable {



    public DelayTaxiProgram(DelayTaxiParameters params) {
        super(params);
//        this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    }

    public DelayTaxiProgram(DelayTaxiParameters params, FlinkDeploymentConfig config) {
        super(params, config);
//        this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {
        DataStream<Map<String, Object>> dataStream = messageStream[0];
//
        DataStream<Map<String, Object>> result = dataStream
                .map(new MapFunction<Map<String, Object>, Tuple2<String, Map<String, Object>>>() {
                    @Override
                    public Tuple2<String, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of((String) value.get("grid-cell-id"), value);
                    }
                })
//                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple2<Integer, Map<String, Object>>>() {
//
//                    @Override
//                    public long extractAscendingTimestamp(Tuple2<Integer, Map<String, Object>> element) {
//                        return (long) element.f1.get("window_time_end");
//                    }
//                })
                .keyBy(0)
//                .window(TumblingEventTimeWindows.of(Time.hours(2))
                .flatMap(new DelayTaxi(this.params));

        return result;
    }


}

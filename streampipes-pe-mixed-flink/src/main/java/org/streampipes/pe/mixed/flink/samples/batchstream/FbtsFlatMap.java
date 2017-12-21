package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.util.Map;

public class FbtsFlatMap extends RichFlatMapFunction<Tuple2<Integer, Map<String, Object>>, Map<String, Object>> {

    private transient ValueState<Tuple2<Boolean, Long>> state;

    @Override
    public void flatMap(Tuple2<Integer, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {
        int key = (int) value.f1.get("fbtskey");

        // 0: batch data, 1: stream data
        if (key == 0) {
            state.update(Tuple2.of(true, System.currentTimeMillis()));

            value.f1.remove("fbtskey");
            out.collect(value.f1);
        } else {

            Tuple2<Boolean, Long> currentState = state.value();
            boolean sendStream = false;

            if (currentState.f1 == Long.MIN_VALUE ) {
                sendStream = true;
            } else {
                // Start to send stream events when there was no batch event in 1 second
                if (currentState.f0 && System.currentTimeMillis() - currentState.f1 >= 1000) {
                    sendStream = true;
                    state.update(Tuple2.of(true, Long.MIN_VALUE));
                }
            }

            if (sendStream) {
                value.f1.remove("fbtskey");
                out.collect(value.f1);
            }
        }
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<Tuple2<Boolean, Long>> descriptor =
                new ValueStateDescriptor<Tuple2<Boolean, Long>>("time",
                        TypeInformation.of(new TypeHint<Tuple2<Boolean,Long>>() {}),
                        Tuple2.of(false, System.currentTimeMillis()));

        state = getRuntimeContext().getState(descriptor);
    }
}

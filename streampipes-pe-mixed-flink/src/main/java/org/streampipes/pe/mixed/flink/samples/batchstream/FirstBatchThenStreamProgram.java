package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

public class FirstBatchThenStreamProgram extends FlinkDataProcessorRuntime<FirstBatchThenStreamParameters> implements Serializable {

    public FirstBatchThenStreamProgram(FirstBatchThenStreamParameters params) {
        super(params);
    }

    public FirstBatchThenStreamProgram(FirstBatchThenStreamParameters params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
        DataStream<Map<String, Object>> batch = messageStream[0];
        DataStream<Map<String, Object>> stream = messageStream[1];

        batch = batch.map(new AddUnionKeyMap(0));
        stream = stream.map(new AddUnionKeyMap(1));

//        KeyedStream<Tuple2<Integer, Map<String, Object>>, Integer> union = batch
        return batch
                .union(stream)
                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
                    @Override
                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of(0, value);
                    }
                })
                .keyBy(0)
                .flatMap(new FbtsFlatMap());

//        return union.flatMap(new FbtsFlatMap());
    }

}

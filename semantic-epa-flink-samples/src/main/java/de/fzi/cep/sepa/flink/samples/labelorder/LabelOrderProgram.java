package de.fzi.cep.sepa.flink.samples.labelorder;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

public class LabelOrderProgram extends FlinkSepaRuntime<LabelOrderParameters> implements Serializable {

    public LabelOrderProgram(LabelOrderParameters params) {
        super(params);

    }

    public LabelOrderProgram(LabelOrderParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {

        DataStream<Tuple2<Integer, Map<String, Object>>> orderStream = messageStream[0]
                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
                    @Override
                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of(0, value);
                    }
                });

        DataStream<Tuple2<Integer, Map<String, Object>>> maintenanceStream = messageStream[1]
                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
                    @Override
                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
                        return Tuple2.of(0, value);
                    }
                });



        ConnectedStreams<Tuple2<Integer, Map<String, Object>>, Tuple2<Integer, Map<String, Object>>> connectedStream =
                orderStream.connect(maintenanceStream);


//        connectedStream.flatMap(new CoFlatMapFunction<Map<String,Object>, Map<String,Object>, Map<String,Object>>() {
//
//            @Override
//            //order stream
//            public void flatMap1(Map<String, Object> value, Collector<Map<String, Object>> out) throws Exception {
//                System.out.println("Order " + value);
//
//                out.collect(value);
//            }
//
//            @Override
//            //maintanence stream
//            public void flatMap2(Map<String, Object> value, Collector<Map<String, Object>> out) throws Exception {
//                System.out.println("Maintenance " + value);
//                //TODO quickfix remove later
//                if (value != null) {
//
//                }
//                out.collect(value);
//            }
//        });






// TODO here I have two streams
        DataStream<Map<String, Object>> result = connectedStream
                .keyBy(0, 0)
//                .map(new MapFunction<Map<String, Object>, Tuple2<Integer, Map<String, Object>>>() {
//                    @Override
//                    public Tuple2<Integer, Map<String, Object>> map(Map<String, Object> value) throws Exception {
//                        return Tuple2.of(0, value);
//                    }
//                })
//                .keyBy(0)
                .flatMap(new LabelOrder(this.params));


        result.print();

        return result;
    }
}

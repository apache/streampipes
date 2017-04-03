package de.fzi.cep.sepa.axoom.hmi.sepa;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LabelOrder extends RichCoFlatMapFunction<Tuple2<Integer, Map<String, Object>>, Tuple2<Integer, Map<String, Object>>, Map<String,Object>> implements Serializable {

    private transient ListState<Map<String, Object>> state;

    public LabelOrder(LabelOrderParameters params) {}

    /**
     * Orders
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap2(Tuple2<Integer, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {
        state.add(value.f1);
        out.collect(value.f1);
    }

    /**
     * Maintenance
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap1(Tuple2<Integer, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {
//        List<Map<String, Object>> currentState =  state.value();
//        System.out.println("Maintenance " + value);

//        long timeStamp = (long) value.f1.get("maintenanceEndTime");
        long timeStamp = (long) value.f1.get("maintenanceStartTime");

        Iterator<Map<String, Object>> iter = state.get().iterator();

        while (iter.hasNext()) {
            Map<String, Object> tmp = iter.next();
            long timediff = timeStamp - (long) tmp.get("timestamp");

            if (timediff < 0) {
                timediff = 0;
            }

            tmp.put("nextMaintenance", TimeUnit.MILLISECONDS.toMinutes(timediff));
            out.collect(tmp);

        }

        state.clear();

    }


    @Override
    public void open(Configuration config) {
        ListStateDescriptor <Map<String, Object>> descriptor =
                new ListStateDescriptor("oldEvents",
                        TypeInformation.of(new TypeHint<List<Map<String, Object>>>(){}));
        state = this.getRuntimeContext().getListState(descriptor);
    }
}

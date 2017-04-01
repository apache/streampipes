package de.fzi.cep.sepa.flink.samples.delay.taxi;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DelayTaxi extends RichFlatMapFunction<Tuple2<Integer, Map<String, Object>>, Map<String, Object>> implements Serializable {

    private static String internal_timestamp_string = "internal_emit_timestamp";
    private int delayValue;
    private String labelPropertyMapping;

    /**
     * The state contains a list with the old values that need to be emitted when
     * the delay is over
     */
    private transient ValueState<Map<String, List<Map<String, Object>>>> state;


    public DelayTaxi(DelayTaxiParameters params) {
        this.delayValue = params.getDelayValue();
        this.labelPropertyMapping = params.getLabelPropertyMapping();
    }

    @Override
    public void flatMap(Tuple2<Integer, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {
        Map<String, List<Map<String, Object>>> currentState =  state.value();

        //TODO make it configuratble what is used for the time
        long currentTime = System.currentTimeMillis();


        out.collect(value.f1);

        // add emit timestamt to the new event
        long emitTimestamp = System.currentTimeMillis() + (delayValue * 60000);
        value.f1.put(internal_timestamp_string, emitTimestamp);


        String cellId = value.f1.get("cellId").toString();
        if (currentState.containsKey(cellId)) {
            currentState.get(cellId).add(value.f1);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(value.f1);
            currentState.put(cellId, list);
        }


        // maybe emit labeled vector

        if (currentState.containsKey(cellId)) {
            List<Map<String, Object>> list = currentState.get(cellId);

            for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {

                Map<String, Object> element = iterator.next();

                long elementTime = (long) element.get(internal_timestamp_string);
                if (Math.abs(elementTime - currentTime) < 300000) {
                    element.remove(internal_timestamp_string);
                    element.put(DelayTaxiController.OUTPUT_LABEL, value.f1.get(labelPropertyMapping));
                    out.collect(element);
                    iterator.remove();
                } else if (elementTime < currentTime) {
                    iterator.remove();
                }
            }



        }

        state.update(currentState);
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<Map<String, List<Map<String, Object>>>> descriptor =
                new ValueStateDescriptor(
                        "oldEvents",
                        TypeInformation.of(new TypeHint<Map<String, List<Map<String, Object>>>>() {}),
                        new ArrayList<>()
                );
        state = this.getRuntimeContext().getState(descriptor);
    }
}

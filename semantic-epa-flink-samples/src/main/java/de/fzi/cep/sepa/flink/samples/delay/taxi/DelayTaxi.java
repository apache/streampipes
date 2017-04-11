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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DelayTaxi extends RichFlatMapFunction<Tuple2<String, Map<String, Object>>, Map<String, Object>> implements Serializable {
//public class DelayTaxi extends RichWindowFunction<Tuple2<String, Map<String, Object>>, Tuple2<String, Map<String, Object>>, Tuple, TimeWindow> {

    private static String internal_timestamp_string = "internal_emit_timestamp";
    private String labelPropertyMapping;

    /**
     * The state contains a list with the old values that need to be emitted when
     * the delay is over
     */
    private transient ValueState<List<Map<String, Object>>> state;


    public DelayTaxi(DelayTaxiParameters params) {
        this.labelPropertyMapping = params.getLabelPropertyMapping();
    }

    @Override
    public void flatMap(Tuple2<String, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {
        List<Map<String, Object>> currentState =  state.value();

        //TODO remove comment
//        out.collect(value.f1);

        //This variable is needed to check whether the current value should be added to the state or not
        boolean addToState = true;

        for (Iterator<Map<String, Object>> iterator = currentState.iterator(); iterator.hasNext();) {
            Map<String, Object> listElement = iterator.next();

            Map<String, Object> smaller;
            Map<String, Object> bigger;

            if ((long) value.f1.get("window_time_end") < (long) listElement.get("window_time_end")) {
                smaller = value.f1;
                bigger = listElement;
            } else {
                smaller = listElement;
                bigger = value.f1;
            }

            if ((long) bigger.get("window_time_end") - (long) smaller.get("window_time_end") < 3700000) {
                smaller.put(DelayTaxiController.OUTPUT_LABEL, bigger.get(labelPropertyMapping));
                out.collect(smaller);

//                System.out.println("===================== Delay Taxi ===========================");
//                System.out.println("Window end time: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(smaller.get("window_time_end")));
//                System.out.println("Count Aggregate: " + smaller.get("aggregate_taxi_count"));
//                System.out.println("New Label: " + smaller.get("delay_label"));
//                System.out.println("State size: " + currentState.size());
//                System.out.println("============================================================");


            }

            // CLean up state when data is too old
            if ((long) listElement.get("window_time_end") < (long) bigger.get("window_time_end") - 172800000) {
                iterator.remove();
            }
        }

        currentState.add(value.f1);

        state.update(currentState);
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<List<Map<String, Object>>> descriptor =
                new ValueStateDescriptor(
                        "oldEvents",
                        TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {}),
                        new ArrayList<>()
                );
        state = this.getRuntimeContext().getState(descriptor);
    }
}

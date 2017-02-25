package de.fzi.cep.sepa.flink.samples.delay;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.Map;

public class SendToKafkaFlatMap extends RichFlatMapFunction<Tuple2<Integer, Map<String, Object>>, Map<String, Object>> implements Serializable {

    private int delayValue;
    private String labelName;
    private String labelPropertyMapping;

    /**
     * The state contains a list with the old values that need to be emitted when
     * the delay is over
     */
    private transient ListState<Map<String, Object>> state;


    public SendToKafkaFlatMap(DelayParameters params) {
        this.delayValue = params.getDelayValue();
        this.labelName = params.getLabelName();
        this.labelPropertyMapping = params.getLabelPropertyMapping();

    }

    @Override
    public void flatMap(Tuple2<Integer, Map<String, Object>> value, Collector<Map<String, Object>> out) throws Exception {

        //TODO make it configuratble what is used for the time
        long currentTime = System.currentTimeMillis();

        // emit the unlabeled event
        out.collect(value.f1);

        // add emit timestamt to the new event
        long emitTimestamp = System.currentTimeMillis() + (delayValue * 60000);
        value.f1.put("internal_emit_timestamp", emitTimestamp);

        // append new event to the state
        state.add(value.f1);

        // get the last event from state and check emit timestamp
        Map<String, Object> lastElement = state.get().iterator().next();
        System.out.println(lastElement.toString());

        // append label to the old event

        // emit the old event with label

    }

    @Override
    public void open(Configuration config) {
        ListStateDescriptor<Map<String, Object>> descriptor = new ListStateDescriptor<>(
                "oldEvents",
                TypeInformation.of(new TypeHint<Map<String, Object>>() {}));
        state = this.getRuntimeContext().getListState(descriptor);
    }
}

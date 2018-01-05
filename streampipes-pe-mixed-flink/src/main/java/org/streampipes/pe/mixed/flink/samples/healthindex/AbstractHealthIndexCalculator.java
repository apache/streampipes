package org.streampipes.pe.mixed.flink.samples.healthindex;

import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHealthIndexCalculator implements AllWindowFunction<Map<String, Object>, Map<String, Object>, GlobalWindow> {

    protected String frictionValueKey;
    protected String timestampKey;
    protected String machineTypeKey;

    public AbstractHealthIndexCalculator(String frictionValueKey, String timestampKey, String machineTypeKey) {
        this.frictionValueKey = frictionValueKey;
        this.timestampKey = timestampKey;
        this.machineTypeKey = machineTypeKey;
    }

    protected Map<String, Object> makeOutputEvent(long timestamp, double healthIndexValue, String machineType) {
        Map<String, Object> outputEvent = new HashMap<>();
        outputEvent.put("timestamp", timestamp);
        outputEvent.put("healthIndex", healthIndexValue);
        outputEvent.put("machineId", machineType);

        return outputEvent;
    }
}

package org.streampipes.connect;

import org.streampipes.connect.firstconnector.Adapter;

import java.util.HashMap;
import java.util.Map;

public enum RunningAdapterInstances {
    INSTANCE;

    private final Map<String, Adapter> runningInstances = new HashMap<>();

    public void addAdapter(String id, Adapter adapter) {
        runningInstances.put(id, adapter);
    }

    public Adapter removeAdapter(String id) {
        Adapter result = runningInstances.get(id);
        runningInstances.remove(id);
        return result;
    }


}

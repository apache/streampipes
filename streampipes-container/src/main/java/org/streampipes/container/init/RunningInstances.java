package org.streampipes.container.init;

import java.util.HashMap;
import java.util.Map;

import org.streampipes.container.util.ElementInfo;
import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.base.NamedStreamPipesEntity;

public enum RunningInstances {
    INSTANCE;

    private final Map<String, ElementInfo> runningInstances = new HashMap<>();


    public void add(String id, NamedStreamPipesEntity description, InvocableDeclarer invocation) {
        runningInstances.put(id, new ElementInfo(description, invocation));
    }

    public InvocableDeclarer getInvocation(String id) {
        ElementInfo result = runningInstances.get(id);
        if (result != null) {
            return result.getInvocation();
        } else {
            return null;
        }
    }

    public NamedStreamPipesEntity getDescription(String id) {
        return runningInstances.get(id).getDescription();
    }

    public void remove(String id) {
        runningInstances.remove(id);

    }
}

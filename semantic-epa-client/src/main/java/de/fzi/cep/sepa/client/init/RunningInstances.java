package de.fzi.cep.sepa.client.init;

import java.util.HashMap;
import java.util.Map;

import de.fzi.cep.sepa.client.util.ElementInfo;
import de.fzi.cep.sepa.client.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;

public enum RunningInstances {
    INSTANCE;

    private final Map<String, ElementInfo> runningInstances = new HashMap<>();


    public void add(String id, NamedSEPAElement description, InvocableDeclarer invocation) {
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

    public NamedSEPAElement getDescription(String id) {
        return runningInstances.get(id).getDescription();
    }

    public void remove(String id) {
        runningInstances.remove(id);

    }
}

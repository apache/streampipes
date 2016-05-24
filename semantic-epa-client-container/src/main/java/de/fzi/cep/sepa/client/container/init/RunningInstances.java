package de.fzi.cep.sepa.client.container.init;

import de.fzi.cep.sepa.client.container.utils.ElementInfo;
import de.fzi.cep.sepa.desc.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;

import java.util.HashMap;
import java.util.Map;

public enum RunningInstances {
    INSTANCE;

    private final Map<String, ElementInfo> runningInstances = new HashMap<>();


    public void add(String id, NamedSEPAElement description, InvocableDeclarer invocation) {
        runningInstances.put(id, new ElementInfo(description, invocation));
    }

    public InvocableDeclarer getInvocation(String id) {
        return runningInstances.get(id).getInvocation();
    }

    public NamedSEPAElement getDescription(String id) {
        return runningInstances.get(id).getDescription();
    }

    public void remove(String id) {
        runningInstances.remove(id);

    }
}

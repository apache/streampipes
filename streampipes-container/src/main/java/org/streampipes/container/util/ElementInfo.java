package org.streampipes.container.util;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.NamedSEPAElement;

public class ElementInfo {
    private NamedSEPAElement description;
    private InvocableDeclarer invocation;

    public ElementInfo(NamedSEPAElement description, InvocableDeclarer invocation) {
        this.description = description;
        this.invocation = invocation;
    }

    public NamedSEPAElement getDescription() {
        return description;
    }

    public void setDescription(NamedSEPAElement description) {
        this.description = description;
    }

    public InvocableDeclarer getInvocation() {
        return invocation;
    }

    public void setInvocation(InvocableDeclarer invocation) {
        this.invocation = invocation;
    }
}

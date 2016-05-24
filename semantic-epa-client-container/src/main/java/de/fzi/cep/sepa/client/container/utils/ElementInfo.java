package de.fzi.cep.sepa.client.container.utils;

import de.fzi.cep.sepa.desc.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;

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

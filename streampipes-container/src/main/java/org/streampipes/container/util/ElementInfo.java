package org.streampipes.container.util;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.base.NamedStreamPipesEntity;

public class ElementInfo {
    private NamedStreamPipesEntity description;
    private InvocableDeclarer invocation;

    public ElementInfo(NamedStreamPipesEntity description, InvocableDeclarer invocation) {
        this.description = description;
        this.invocation = invocation;
    }

    public NamedStreamPipesEntity getDescription() {
        return description;
    }

    public void setDescription(NamedStreamPipesEntity description) {
        this.description = description;
    }

    public InvocableDeclarer getInvocation() {
        return invocation;
    }

    public void setInvocation(InvocableDeclarer invocation) {
        this.invocation = invocation;
    }
}

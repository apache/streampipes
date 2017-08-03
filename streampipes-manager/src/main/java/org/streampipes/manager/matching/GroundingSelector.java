package org.streampipes.manager.matching;

import org.streampipes.commons.config.old.BrokerConfig;
import org.streampipes.commons.config.old.Configuration;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by riemer on 23.09.2016.
 */
public abstract class GroundingSelector {

    protected NamedSEPAElement source;
    protected Set<InvocableSEPAElement> targets;
    protected BrokerConfig config;

    public GroundingSelector(NamedSEPAElement source, Set<InvocableSEPAElement> targets) {
        this.source = source;
        this.targets = targets;
        this.config = Configuration
                .getInstance()
                .getBrokerConfig();
    }

    protected List<InvocableSEPAElement> buildInvocables() {
        List<InvocableSEPAElement> elements = new ArrayList<>();
        elements.add((InvocableSEPAElement) source);
        elements.addAll(targets);

        return elements;
    }
}

package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.commons.config.BrokerConfig;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;

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

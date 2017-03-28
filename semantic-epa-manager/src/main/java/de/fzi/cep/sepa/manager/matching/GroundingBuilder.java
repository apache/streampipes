package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by riemer on 25.09.2016.
 */
public class GroundingBuilder {

    private NamedSEPAElement source;
    private Set<InvocableSEPAElement> targets;

    public GroundingBuilder(NamedSEPAElement source, Set<InvocableSEPAElement> targets) {
        this.source = source;
        this.targets = targets;
    }

    public EventGrounding getEventGrounding() {
        EventGrounding grounding = new EventGrounding();
        grounding.setTransportFormats(Arrays.asList(getFormat()));
        grounding.setTransportProtocols(Arrays.asList(getProtocol()));
        return grounding;
    }

    private TransportFormat getFormat() {
        return new FormatSelector(source, targets).getTransportFormat();
    }

    private TransportProtocol getProtocol() {
        return new ProtocolSelector(source, targets).getPreferredProtocol();
    }
}

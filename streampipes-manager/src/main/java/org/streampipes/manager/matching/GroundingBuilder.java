package org.streampipes.manager.matching;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;

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

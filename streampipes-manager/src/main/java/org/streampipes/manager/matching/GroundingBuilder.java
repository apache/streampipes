package org.streampipes.manager.matching;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by riemer on 25.09.2016.
 */
public class GroundingBuilder {

    private NamedStreamPipesEntity source;
    private Set<InvocableStreamPipesEntity> targets;

    public GroundingBuilder(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
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

package org.streampipes.manager.matching;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.vocabulary.MessageFormat;

import java.util.List;
import java.util.Set;

/**
 * Created by riemer on 23.09.2016.
 */
public class FormatSelector extends GroundingSelector {

    public FormatSelector(NamedSEPAElement source, Set<InvocableSEPAElement> targets) {
        super(source, targets);
    }

    public TransportFormat getTransportFormat() {

        if (source instanceof EventStream) {
            return ((EventStream) source)
                    .getEventGrounding()
                    .getTransportFormats()
                    .get(0);
        } else {
            if (supportsFormat(MessageFormat.Json)) {
                return new TransportFormat(MessageFormat.Json);
            } else if (supportsFormat(MessageFormat.Thrift)) {
                return new TransportFormat(MessageFormat.Thrift);
            }
        }
        return new TransportFormat(MessageFormat.Json);
    }

    public <T extends TransportFormat> boolean supportsFormat(String format) {
        List<InvocableSEPAElement> elements = buildInvocables();
        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportFormats()
                        .stream()
                        .anyMatch(s -> s.getRdfType().contains(format)));
    }
}

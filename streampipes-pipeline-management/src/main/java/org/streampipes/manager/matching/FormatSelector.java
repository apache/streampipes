package org.streampipes.manager.matching;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;

import java.util.List;
import java.util.Set;

public class FormatSelector extends GroundingSelector {

    public FormatSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        super(source, targets);
    }

    public TransportFormat getTransportFormat() {

        if (source instanceof SpDataStream) {
            return ((SpDataStream) source)
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
        List<InvocableStreamPipesEntity> elements = buildInvocables();
        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportFormats()
                        .stream()
                        .anyMatch(s -> s.getRdfType().contains(format)));
    }
}

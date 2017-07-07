package org.streampipes.manager.matching;

import org.streampipes.manager.util.TopicGenerator;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportProtocol;

import java.util.List;
import java.util.Set;

/**
 * Created by riemer on 23.09.2016.
 */
public class ProtocolSelector extends GroundingSelector {

    private String outputTopic;

    public ProtocolSelector(NamedSEPAElement source, Set<InvocableSEPAElement> targets) {
        super(source, targets);
        this.outputTopic = TopicGenerator.generateRandomTopic();
    }

    public TransportProtocol getPreferredProtocol() {
        if (source instanceof EventStream) {
            return ((EventStream) source)
                    .getEventGrounding()
                    .getTransportProtocol();
        } else {
            if (supportsProtocol(KafkaTransportProtocol.class)) {
                return kafkaTopic();
            } else if (supportsProtocol(JmsTransportProtocol.class)) {
                return new JmsTransportProtocol(config.getJmsHost(),
                        config.getJmsPort(),
                        outputTopic);
            }
        }
        return kafkaTopic();
    }

    private TransportProtocol kafkaTopic() {
        return new KafkaTransportProtocol(config.getKafkaHost(),
                config.getKafkaPort(),
                outputTopic,
                config.getZookeeperHost(),
                config.getZookeeperPort());
    }


    public <T extends TransportProtocol> boolean supportsProtocol(Class<T> protocol) {
        List<InvocableSEPAElement> elements = buildInvocables();

        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportProtocols()
                        .stream()
                        .anyMatch(p -> protocol.isInstance(p)));

    }
}

package org.streampipes.manager.matching;

import org.streampipes.config.backend.BackendConfig;
import org.streampipes.manager.util.TopicGenerator;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.List;
import java.util.Set;

public class ProtocolSelector extends GroundingSelector {

    private String outputTopic;

    public ProtocolSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        super(source, targets);
        this.outputTopic = TopicGenerator.generateRandomTopic();
    }

    public TransportProtocol getPreferredProtocol() {
        if (source instanceof SpDataStream) {
            return ((SpDataStream) source)
                    .getEventGrounding()
                    .getTransportProtocol();
        } else {
            if (supportsProtocol(KafkaTransportProtocol.class)) {
                return kafkaTopic();
            } else if (supportsProtocol(JmsTransportProtocol.class)) {
                return new JmsTransportProtocol(BackendConfig.INSTANCE.getJmsHost(),
                        BackendConfig.INSTANCE.getJmsPort(),
                        outputTopic);
            }
        }
        return kafkaTopic();
    }

    private TransportProtocol kafkaTopic() {
        return new KafkaTransportProtocol(BackendConfig.INSTANCE.getKafkaHost(),
                BackendConfig.INSTANCE.getKafkaPort(),
                outputTopic,
                BackendConfig.INSTANCE.getZookeeperHost(),
                BackendConfig.INSTANCE.getZookeeperPort());
    }


    public <T extends TransportProtocol> boolean supportsProtocol(Class<T> protocol) {
        List<InvocableStreamPipesEntity> elements = buildInvocables();

        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportProtocols()
                        .stream()
                        .anyMatch(p -> protocol.isInstance(p)));

    }
}

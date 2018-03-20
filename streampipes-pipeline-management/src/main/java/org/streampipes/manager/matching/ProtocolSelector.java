/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

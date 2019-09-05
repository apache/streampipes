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
import org.streampipes.config.backend.SpDataFormat;
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
            List<SpDataFormat> prioritizedFormats =
                    BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedFormats();

            return prioritizedFormats
                    .stream()
                    .filter(pf -> supportsFormat(pf.getMessageFormat()))
                    .findFirst()
                    .map(pf -> new TransportFormat(pf.getMessageFormat()))
                    .orElse(new TransportFormat(MessageFormat.Json));
        }
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

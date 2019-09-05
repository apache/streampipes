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

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.Collections;
import java.util.Set;

public class GroundingBuilder {

    private NamedStreamPipesEntity source;
    private Set<InvocableStreamPipesEntity> targets;

    public GroundingBuilder(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        this.source = source;
        this.targets = targets;
    }

    public EventGrounding getEventGrounding() {
        EventGrounding grounding = new EventGrounding();
        grounding.setTransportFormats(Collections.singletonList(getFormat()));
        grounding.setTransportProtocols(Collections.singletonList(getProtocol()));
        return grounding;
    }

    private TransportFormat getFormat() {
        return new FormatSelector(source, targets).getTransportFormat();
    }

    private TransportProtocol getProtocol() {
        return new ProtocolSelector(source, targets).getPreferredProtocol();
    }
}

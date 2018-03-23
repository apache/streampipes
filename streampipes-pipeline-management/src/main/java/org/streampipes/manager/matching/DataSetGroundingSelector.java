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
import org.streampipes.model.SpDataSet;
import org.streampipes.model.client.pipeline.DataSetModificationMessage;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.Arrays;

public class DataSetGroundingSelector {

  private SpDataSet spDataSet;

  public DataSetGroundingSelector(SpDataSet spDataSet) {
    this.spDataSet = spDataSet;
  }

  public DataSetModificationMessage selectGrounding() {
    // TODO grounding negotiation
    TransportProtocol protocol = new KafkaTransportProtocol(BackendConfig.INSTANCE.getKafkaHost(),
            BackendConfig.INSTANCE.getKafkaPort(),
            TopicGenerator.generateRandomTopic(),
            BackendConfig.INSTANCE.getZookeeperHost(),
            BackendConfig.INSTANCE.getZookeeperPort());
    TransportFormat format = spDataSet.getSupportedGrounding().getTransportFormats().get(0);

    EventGrounding outputGrounding = new EventGrounding();
    outputGrounding.setTransportProtocol(protocol);
    outputGrounding.setTransportFormats(Arrays.asList(format));

    return new DataSetModificationMessage(outputGrounding);
  }
}

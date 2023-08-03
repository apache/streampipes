/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.client.live;

import org.apache.streampipes.client.api.live.IConfiguredEventProducer;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.EventGrounding;

public class ProducerManager {

  private final EventGrounding grounding;

  public ProducerManager(EventGrounding grounding) {
    this.grounding = grounding;
  }

  public IConfiguredEventProducer makeProducer() {
    EventProducer producer = findProducer();
    producer.connect();

    return new ConfiguredEventProducer(
        producer,
        findFormatDefinition()
    );
  }

  private EventProducer findProducer() {
    var protocol = grounding.getTransportProtocol();
    return SpProtocolManager
        .INSTANCE
        .findDefinition(protocol)
        .orElseThrow()
        .getProducer(protocol);
  }

  private SpDataFormatDefinition findFormatDefinition() {
    var format = grounding.getTransportFormats().get(0);
    return SpDataFormatManager
        .INSTANCE
        .findDefinition(format)
        .orElseThrow();
  }
}

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

import org.apache.streampipes.client.api.live.EventProcessor;
import org.apache.streampipes.client.api.live.IBrokerConfigOverride;
import org.apache.streampipes.client.api.live.ISubscription;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;

import java.util.NoSuchElementException;

public class SubscriptionManager {

  private final EventGrounding grounding;
  private final EventProcessor callback;

  private IBrokerConfigOverride brokerConfigOverride;
  private boolean overrideSettings = false;

  public SubscriptionManager(EventGrounding grounding,
                             EventProcessor callback) {
    this.grounding = grounding;
    this.callback = callback;
  }

  public SubscriptionManager(IBrokerConfigOverride brokerConfigOverride,
                             EventGrounding grounding,
                             EventProcessor callback) {
    this(grounding, callback);
    this.brokerConfigOverride = brokerConfigOverride;
    this.overrideSettings = true;
  }

  public ISubscription subscribe() {
    var formatDefinitionOpt = SpDataFormatManager
        .INSTANCE
        .findDefinition(this.grounding.getTransportFormats().get(0));

    try {
      SpProtocolDefinition<TransportProtocol> protocolDefinition = findProtocol(getTransportProtocol());

      if (formatDefinitionOpt.isPresent()) {
        final SpDataFormatDefinition converter = formatDefinitionOpt.get();

        var protocol = getTransportProtocol();
        if (overrideSettings) {
          if (protocol instanceof KafkaTransportProtocol) {
            brokerConfigOverride.overrideKafkaHostname((KafkaTransportProtocol) protocol);
          }
          brokerConfigOverride.overrideHostname(protocol);
          brokerConfigOverride.overridePort(protocol);
        }

        EventConsumer consumer = protocolDefinition.getConsumer(protocol);
        consumer.connect(event -> {
          try {
            Event spEvent = EventFactory.fromMap(converter.toMap(event));
            callback.onEvent(spEvent);
          } catch (SpRuntimeException e) {
            e.printStackTrace();
          }
        });

        return new Subscription(consumer);
      } else {
        throw new SpRuntimeException(
            "No converter found for data format - did you add a format factory (client.registerDataFormat)?");
      }
    } catch (NoSuchElementException e) {
      throw new SpRuntimeException(
          "Could not find an implementation for messaging protocol "
              + this.grounding.getTransportProtocol().getClass().getCanonicalName()
              + "- please add the corresponding module (streampipes-messaging-*) to your project dependencies.");

    }
  }

  private SpProtocolDefinition<TransportProtocol> findProtocol(TransportProtocol protocol) {
    return SpProtocolManager.INSTANCE.findDefinition(protocol).orElseThrow();
  }

  private TransportProtocol getTransportProtocol() {
    return this.grounding.getTransportProtocol();
  }
}

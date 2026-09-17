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

package org.apache.streampipes.wrapper.standalone.routing;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.limiter.SpRateLimiter;
import org.apache.streampipes.extensions.api.memorymanager.SpMemoryManager;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandaloneSpInputCollector<T extends TransportProtocol> extends
    StandaloneSpCollector<T, RawDataProcessor>
    implements
    InternalEventProcessor<byte[]>, SpInputCollector {

  private final Boolean singletonEngine;
  private final EventConsumer consumer;
  private final boolean loadManagementEnabled;

  public StandaloneSpInputCollector(T protocol,
                                    Boolean singletonEngine) throws SpRuntimeException {
    super(protocol);
    this.consumer = protocolDefinition.getConsumer(protocol);
    this.singletonEngine = singletonEngine;
    this.loadManagementEnabled = Environments.getEnvironment().getLoadManagerEnable().getValueOrDefault();
  }

  @Override
  public void onEvent(byte[] event) throws InterruptedException {
    if (consumers.isEmpty()) {
      return;
    }
    if (loadManagementEnabled) {
      SpRateLimiter.INSTANCE.acquire(event.length);
      try (var reservation = SpMemoryManager.INSTANCE.reserve(event.length)) {
        dispatch(event);
      }
    } else {
      dispatch(event);
    }
  }

  private void dispatch(byte[] event) {
    // Resolve recipients after admission: consumers may detach while we wait.
    var recipients = new ArrayList<>(consumers.entrySet());
    if (recipients.isEmpty()) {
      return;
    }
    var decoded = dataFormatDefinition.toMap(event);
    int recipientCount = singletonEngine ? 1 : recipients.size();
    for (int i = 0; i < recipientCount; i++) {
      var recipient = recipients.get(i);
      // A preceding callback may take long enough for this registration to be
      // removed or replaced. Do not dispatch to a stale runtime from the snapshot.
      if (consumers.get(recipient.getKey()) != recipient.getValue()) {
        continue;
      }
      // Only the last consumer may mutate the original decoded payload.
      var input = i == recipientCount - 1 ? decoded : copyEvent(decoded);
      recipient.getValue().process(input, event.length, topic);
    }
  }

  private Map<String, Object> copyEvent(Map<String, Object> event) {
    Map<String, Object> copy = new HashMap<>(event.size());
    event.forEach((key, value) -> copy.put(key, copyValue(value)));
    return copy;
  }

  private Object copyValue(Object value) {
    if (value instanceof Map<?, ?> map) {
      Map<Object, Object> copy = new HashMap<>(map.size());
      map.forEach((key, item) -> copy.put(key, copyValue(item)));
      return copy;
    } else if (value instanceof List<?> list) {
      List<Object> copy = new ArrayList<>(list.size());
      list.forEach(item -> copy.add(copyValue(item)));
      return copy;
    }
    // The JSON codec produces immutable scalar values (including null).
    return value;
  }

  @Override
  public void connect() throws SpRuntimeException {
    if (!consumer.isConnected()) {
      consumer.connect(this);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (consumer.isConnected()) {
      if (consumers.isEmpty()) {
        consumer.disconnect();
        ProtocolManager.removeInputCollector(transportProtocol);
      }
    }
  }
}

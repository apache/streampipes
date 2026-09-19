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
package org.apache.streampipes.manager.runtime;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalBrokerProvider;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.SpDataStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStreamRuntimeInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(DataStreamRuntimeInfoProvider.class);

  private final Map<String, SpDataStream> dataStreams;
  private final List<EventConsumer> consumers;
  private final Map<String, Map<String, Object>> latestEvents;

  public DataStreamRuntimeInfoProvider(Map<String, SpDataStream> dataStreams) {
    this.dataStreams = dataStreams;
    this.consumers = new ArrayList<>();
    this.latestEvents = new HashMap<>();
  }

  public void startConsuming() throws SpRuntimeException {
    dataStreams.forEach((id, dataStream) -> {
      var protocol = InternalBrokerProvider.resolve(dataStream.getEventGrounding().getTransportProtocol());
      var converter = new SpDataFormatConverter(SpDataFormatManager.getFormatDefinition());
      var protocolDefinitionOpt = SpProtocolManager
          .INSTANCE
          .findDefinition(protocol);

      if (protocolDefinitionOpt.isPresent()) {
        var consumer = protocolDefinitionOpt.get().getConsumer(protocol);
        consumer.connect(event -> {
          var deserializedEvent = converter.convert(event);
          this.latestEvents.put(id, deserializedEvent);
        });
        consumers.add(consumer);
      } else {
        LOG.error("Error while fetching data for preview - protocol {} not found - did you register the protocol? ",
            protocol.getClass().getCanonicalName());
        throw new SpRuntimeException("Protocol not found");
      }
    });
  }

  public Map<String, Map<String, Object>> getLatestEvents() {
    return latestEvents;
  }

  public void close() {
    consumers.forEach(EventConsumer::disconnect);
  }
}

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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.management.monitoring.ExtensionsLogger;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventConverter;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StandaloneSpOutputCollector<T extends TransportProtocol> extends
    StandaloneSpCollector<T, InternalEventProcessor<Map<String,
        Object>>> implements SpOutputCollector {

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneSpOutputCollector.class);

  private final EventProducer producer;
  private final String resourceId;
  private final ExtensionsLogger extensionsLogger;

  public StandaloneSpOutputCollector(T protocol,
                                     TransportFormat format,
                                     String resourceId) throws SpRuntimeException {
    super(protocol, format);
    this.producer = protocolDefinition.getProducer(protocol);
    this.resourceId = resourceId;
    this.extensionsLogger = new ExtensionsLogger(resourceId);
  }

  public void collect(Event event) {
    Map<String, Object> outEvent = new EventConverter(event).toMap();
    try {
      producer.publish(dataFormatDefinition.fromMap(outEvent));
      SpMonitoringManager.INSTANCE.increaseOutCounter(resourceId, System.currentTimeMillis());
    } catch (SpRuntimeException e) {
      extensionsLogger.error(e);
      LOG.error("Could not publish event", e);
    }
  }

  @Override
  public void connect() throws SpRuntimeException {
    if (!producer.isConnected()) {
      producer.connect();
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (producer.isConnected()) {
      producer.disconnect();
      ProtocolManager.removeOutputCollector(transportProtocol);
    }
  }

}

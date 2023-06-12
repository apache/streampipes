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
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.routing.PipelineElementCollector;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.manager.PManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StandaloneSpCollector<T extends TransportProtocol, W> implements
    PipelineElementCollector<W> {

  protected Map<String, W> consumers;

  protected T transportProtocol;
  protected SpProtocolDefinition<T> protocolDefinition;

  protected TransportFormat transportFormat;
  protected SpDataFormatDefinition dataFormatDefinition;
  protected String topic;


  public StandaloneSpCollector(T protocol, TransportFormat format) throws SpRuntimeException {
    this.transportProtocol = protocol;
    this.protocolDefinition = PManager.getProtocolDefinition(protocol).orElseThrow(() -> new
        SpRuntimeException("Could not find protocol"));
    this.transportFormat = format;
    this.dataFormatDefinition = PManager.getDataFormat(format).orElseThrow(() -> new
        SpRuntimeException("Could not find format"));
    this.consumers = new ConcurrentHashMap<>();
    this.topic = transportProtocol.getTopicDefinition().getActualTopicName();
  }

  public void registerConsumer(String routeId, W consumer) {
    consumers.put(routeId, consumer);
  }

  public void unregisterConsumer(String routeId) {
    consumers.remove(routeId);
  }

}

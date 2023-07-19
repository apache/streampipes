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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public enum PipelineElementRuntimeInfoFetcher {
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementRuntimeInfoFetcher.class);

  private static final int FETCH_INTERVAL_MS = 300;
  private final Map<String, SpDataFormatConverter> converterMap;
  private final Environment env;

  PipelineElementRuntimeInfoFetcher() {
    this.converterMap = new HashMap<>();
    this.env = Environments.getEnvironment();
  }

  public String getCurrentData(SpDataStream spDataStream) throws SpRuntimeException {
    var topic = getOutputTopic(spDataStream);
    var protocol = spDataStream.getEventGrounding().getTransportProtocol();
    if (env.getSpDebug().getValueOrDefault()) {
      protocol.setBrokerHostname("localhost");
      if (protocol instanceof KafkaTransportProtocol) {
        ((KafkaTransportProtocol) protocol).setKafkaPort(9094);
      }
    }

    if (!converterMap.containsKey(topic)) {
      this.converterMap.put(topic,
          new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }

    var converter = converterMap.get(topic);

    var protocolDefinitionOpt = SpProtocolManager
        .INSTANCE
        .findDefinition(spDataStream.getEventGrounding().getTransportProtocol());

    if (protocolDefinitionOpt.isPresent()) {
      var consumer = protocolDefinitionOpt.get().getConsumer(protocol);
      return getLatestEvent(consumer, converter);

    } else {
      LOG.error("Error while fetching data for preview - protocol {} not found - did you register the protocol? ",
          protocol.getClass().getCanonicalName());
      throw new SpRuntimeException("Protocol not found");
    }
  }

  private TransportFormat getTransportFormat(SpDataStream spDataStream) {
    return spDataStream.getEventGrounding().getTransportFormats().get(0);
  }

  private String getOutputTopic(SpDataStream spDataStream) {
    return spDataStream
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }

  private void waitForEvent(String[] result) {
    long timeout = 0;
    while (result[0] == null && timeout < 6000) {
      try {
        TimeUnit.MILLISECONDS.sleep(FETCH_INTERVAL_MS);
        timeout = timeout + 300;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private String getLatestEvent(EventConsumer consumer,
                                SpDataFormatConverter converter) {
    final String[] result = {null};
    consumer.connect(event -> {
      result[0] = converter.convert(event);
      consumer.disconnect();
    });

    waitForEvent(result);

    return result[0];
  }
}

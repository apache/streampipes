/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.wrapper.flink.consumer;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQConsumer;
import org.streampipes.model.grounding.JmsTransportProtocol;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class JmsConsumer implements SourceFunction<Map<String, Object>>, Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(JmsConsumer.class);

  private JmsTransportProtocol protocol;
  private ActiveMQConsumer activeMQConsumer;
  private SpDataFormatDefinition spDataFormatDefinition;
  private Boolean isRunning;
  private Queue<byte[]> queue;

  public JmsConsumer(JmsTransportProtocol protocol, SpDataFormatDefinition spDataFormatDefinition) {
    this.protocol = protocol;
    this.activeMQConsumer = new ActiveMQConsumer();
    this.spDataFormatDefinition = spDataFormatDefinition;
    this.queue = new LinkedBlockingQueue<>();
  }

  @Override
  public void run(SourceContext<Map<String, Object>> sourceContext) throws Exception {
    this.isRunning = true;
    this.activeMQConsumer.connect(protocol, new InternalEventProcessor<byte[]>() {
      @Override
      public void onEvent(byte[] event) {
        queue.add(event);
      }
    });

    while (isRunning) {
      if (!queue.isEmpty()) {
        sourceContext.collect(spDataFormatDefinition.toMap(queue.poll()));
      } else {
        Thread.sleep(100);
      }
    }
  }

  @Override
  public void cancel() {
    try {
      this.activeMQConsumer.disconnect();
      this.isRunning = false;
    } catch (SpRuntimeException e) {
      LOG.error(e.getMessage());
    }

  }
}

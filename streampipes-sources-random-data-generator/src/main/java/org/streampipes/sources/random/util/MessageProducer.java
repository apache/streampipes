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
package org.streampipes.sources.random.util;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.sources.random.config.SourcesConfig;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MessageProducer implements Runnable {

  private static final long SIMULATION_DELAY_MS = SourcesConfig.INSTANCE.getSimulaitonDelayMs();
  private static final int SIMULATION_DELAY_NS = SourcesConfig.INSTANCE.getSimulaitonDelayNs();

  private Map<String, SpKafkaProducer> producerMap;
  private Function<MessageConfig, MessageResult> getMessage;

  public MessageProducer(Function<MessageConfig, MessageResult> getMessage) {
    this.getMessage = getMessage;
    this.producerMap = new HashMap<>();
  }

  @Override
  public void run() {
    for (int i = 0; i < SourcesConfig.INSTANCE.getMaxEvents(); i++) {
      try {
        if (i % 50 == 0) {
          System.out.println(i + " Events (Random Number) sent.");
        }
        MessageResult nextMsg = getMessage.apply(new MessageConfig(System.currentTimeMillis(), i));
        if (nextMsg.getShouldSend()) {
          getKafkaProducer(nextMsg.getTopic()).publish(nextMsg.getMessage());
        }
        Thread.sleep(SIMULATION_DELAY_MS, SIMULATION_DELAY_NS);
        if (i % SourcesConfig.INSTANCE.getSimulationWaitEvery() == 0) {
          Thread.sleep(SourcesConfig.INSTANCE.getSimulationWaitFor());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private SpKafkaProducer getKafkaProducer(String topic) {
    if (producerMap.containsKey(topic)) {
      return producerMap.get(topic);
    } else {
      SpKafkaProducer producer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), topic);
      producerMap.put(topic, producer);
      return producer;
    }
  }
}

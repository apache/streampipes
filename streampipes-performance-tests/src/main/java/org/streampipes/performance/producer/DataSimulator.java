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
package org.streampipes.performance.producer;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.performance.simulation.DataReplayStatusNotifier;

import java.util.Random;
import java.util.UUID;

public class DataSimulator implements Runnable {

  private static final String topic = "";

  private String threadId;
  private SpKafkaProducer kafkaProducer;

  private Long totalNumberOfEvents;
  private Long waitTimeBetweenEvents;
  private Random random;

  private DataReplayStatusNotifier statusNotifier;

  public DataSimulator(String kafkaUrl, Long totalNumberOfEvents, Long waitTimeBetweenEvents, String threadId,
                       DataReplayStatusNotifier statusNotifier) {
    this.kafkaProducer = new SpKafkaProducer(kafkaUrl, topic);
    this.threadId = threadId;

    this.totalNumberOfEvents = totalNumberOfEvents;
    this.waitTimeBetweenEvents = waitTimeBetweenEvents;

    this.statusNotifier = statusNotifier;
    this.random = new Random();
  }

  @Override
  public void run() {
    Long eventCount = 0l;
    do {
      kafkaProducer.publish(nextEvent());
      eventCount++;
    } while (eventCount < totalNumberOfEvents);

    this.kafkaProducer.disconnect();
    statusNotifier.onFinished(threadId);
  }

  private byte[] nextEvent() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    builder.append("\"timestamp\" : ").append(System.currentTimeMillis()).append(",");
    builder.append("\"sensorId\" : \"").append(UUID.randomUUID().toString()).append("\",");
    builder.append("\"pressure\" : ").append(random.nextDouble());
    builder.append("}");

    return builder.toString().getBytes();
  }
}

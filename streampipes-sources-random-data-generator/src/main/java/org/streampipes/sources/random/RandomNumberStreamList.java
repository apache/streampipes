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

package org.streampipes.sources.random;

import org.codehaus.jettison.json.JSONObject;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sources.config.SampleSettings;
import org.streampipes.sources.config.SourcesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomNumberStreamList implements EventStreamDeclarer {

  private SpKafkaProducer kafkaProducer;

  private static final String topic = "de.fzi.random.number.list";

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {

    return DataStreamBuilder.create("random-list", "Random Number (List)", "Random number stream " +
            "to test list-based properties")
            .format(Formats.jsonFormat())
            .protocol(SampleSettings.kafkaProtocol(topic))
            .property(EpProperties.integerEp(Labels.empty(), "timestamp", "http://schema.org/DateTime"))
            .property(EpProperties.integerEp(Labels.empty(), "counter", "http://schema.org/counter"))
            .property(EpProperties.listIntegerEp(Labels.empty(), "listValues", "http://schema.org/random"))
            .build();
  }

  @Override
  public void executeStream() {
    kafkaProducer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), topic);

    Runnable r = new Runnable() {

      @Override
      public void run() {
        Random random = new Random();
        int j = 0;
        for (;;) {
          try {
            if (j % 10000 == 0) {
              System.out.println(j +" Events (Random Number) sent.");
            }
            Optional<byte[]> nextMsg = getMessage(System.currentTimeMillis(), j);
            nextMsg.ifPresent(bytes -> kafkaProducer.publish(bytes));
            Thread.sleep(1000);
            j++;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    };
    Thread thread = new Thread(r);
    thread.start();

  }

  private Optional<byte[]> getMessage(long timestamp, int counter) {
    try {
      JSONObject json = new JSONObject();
      json.put("timestamp", timestamp);
      json.put("counter", counter);
      json.put("listValues", buildRandomList());

      return Optional.of(json.toString().getBytes());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private List<Integer> buildRandomList() {
    Random random = new Random();
    List<Integer> result = new ArrayList<>();
    for(int i = 0; i <= 20; i++) {
      result.add(random.nextInt(20));
    }
    return result;
  }

  @Override
  public boolean isExecutable() {
    return true;
  }
}

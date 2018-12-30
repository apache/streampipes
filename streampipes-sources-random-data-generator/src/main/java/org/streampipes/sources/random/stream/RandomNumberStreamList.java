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

package org.streampipes.sources.random.stream;

import org.codehaus.jettison.json.JSONObject;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sources.random.config.SampleSettings;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;

import java.util.ArrayList;
import java.util.List;

public class RandomNumberStreamList extends RandomNumberStream {

  private SpKafkaProducer kafkaProducer;

  private static final String TOPIC = "de.fzi.random.number.list";

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {

    return DataStreamBuilder.create("random-list", "Random Number (List)", "Random number stream " +
            "to test list-based properties")
            .format(Formats.jsonFormat())
            .protocol(SampleSettings.kafkaProtocol(TOPIC))
            .property(EpProperties.integerEp(Labels.empty(), "timestamp", "http://schema.org/DateTime"))
            .property(EpProperties.integerEp(Labels.empty(), "counter", "http://schema.org/counter"))
            .property(EpProperties.listIntegerEp(Labels.empty(), "listValues", "http://schema.org/random"))
            .build();
  }

  @Override
  protected MessageResult getMessage(MessageConfig messageConfig) {
    try {
      JSONObject json = new JSONObject();
      json.put("timestamp", messageConfig.getTimestamp());
      json.put("counter", messageConfig.getCounter());
      json.put("listValues", buildRandomList());

      return new MessageResult(json.toString().getBytes(), TOPIC);
    } catch (Exception e) {
      return new MessageResult(false);
    }
  }

  private List<Integer> buildRandomList() {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i <= 20; i++) {
      result.add(random.nextInt(20));
    }
    return result;
  }

}

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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sources.random.config.SampleSettings;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;

public class RandomNumberStreamJson extends RandomNumberStream {

  private static final String TOPIC = "SEPA.SEP.Random.Number.Json";

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("org.streampipes.pe.random.number.json", "Random Number "
            + "Stream (JSON)", "Publishes randomly generated data and nothing more.")
            .properties(getEventPropertyDescriptions())
            .format(Formats.jsonFormat())
            .protocol(SampleSettings.kafkaProtocol(TOPIC))
            .build();
  }

  @Override
  protected MessageResult getMessage(MessageConfig messageConfig) {
    try {
      return new MessageResult(
              buildJson(messageConfig.getTimestamp(), messageConfig.getCounter())
                      .toString()
                      .getBytes(), TOPIC);
    } catch (JSONException e) {
      e.printStackTrace();
      return new MessageResult(false);
    }
  }

  private JSONObject buildJson(long timestamp, int counter) throws JSONException {
    JSONObject json = new JSONObject();

    json.put("timestamp", timestamp);
    json.put("randomValue", random.nextInt(100));
    json.put("randomString", randomString());
    json.put("count", counter);
    return json;
  }

  public static void main(String[] args) {
    new RandomNumberStreamJson().executeStream();
  }

}


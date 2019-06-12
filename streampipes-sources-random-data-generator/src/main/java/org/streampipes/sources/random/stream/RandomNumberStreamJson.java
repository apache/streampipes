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
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.cbor.CborDataFormatDefinition;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sources.random.config.SampleSettings;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;

import java.util.HashMap;
import java.util.Map;

public class RandomNumberStreamJson extends RandomNumberStream {

  private static final String TOPIC = "SEPA.SEP.Random.Number.Json";

  private CborDataFormatDefinition cborDataFormatDefinition;

  public RandomNumberStreamJson() {
    super();
    this.cborDataFormatDefinition = new CborDataFormatDefinition();
  }

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("org.streampipes.pe.random.number.json", "Random Number "
            + "Stream (JSON)", "Publishes randomly generated data and nothing more.")
            .providesAssets()
            .properties(getEventPropertyDescriptions())
            .format(Formats.cborFormat())
            .protocol(SampleSettings.kafkaProtocol(TOPIC))
            .build();
  }

  @Override
  protected MessageResult getMessage(MessageConfig messageConfig) {
    try {
      return new MessageResult(
              cborDataFormatDefinition.fromMap(buildJson(messageConfig.getTimestamp(),
                      messageConfig.getCounter())), TOPIC);
    } catch (JSONException | SpRuntimeException e) {
      e.printStackTrace();
      return new MessageResult(false);
    }
  }

  private Map<String, Object> buildJson(long timestamp, int counter) throws JSONException {
    Map<String, Object> json = new HashMap<>();

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


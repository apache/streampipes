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
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.helpers.ValueSpecifications;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.sources.random.config.SourcesConfig;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;
import org.streampipes.vocabulary.Geo;
import org.streampipes.vocabulary.SO;

import java.util.Arrays;
import java.util.List;

public class ComplexRandomStream extends RandomNumberStream {

  private static final String TOPIC = "SEPA.SEP.Random.Complex.Json";

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("complex-stream", "Complex Stream", "Stream used for testing list-based " +
            "properties and " +
            "value " +
            "specifications")
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    "org.streampipes.test.complex"))
            .format(Formats.jsonFormat())
            .property(EpProperties.timestampProperty("timestamp"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testMeasurement")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testDimension")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(EpProperties.stringEp(Labels.withTitle("string", "string description"), "testString",
                    "http://test.de", ValueSpecifications.from("A", "B", "C")))
            .property(EpProperties.stringEp(Labels.withTitle("string2", "string description"), "testString2",
                    "http://test.de", ValueSpecifications.from("A", "B", "C", "D")))
            .property(EpProperties.integerEp(Labels.withTitle("integer2", "integerDescription"), "testInteger2",
                    SO.Number, ValueSpecifications.from(0.0f, 1.0f, 1.f)))
            .property(EpProperties.integerEp(Labels.withTitle("integer", "integerDescription"), "testInteger",
                    SO.Number, ValueSpecifications.from(10.0f, 100.0f, 10.0f)))
            .property(EpProperties.nestedEp(Labels.from("location", "", ""), "location-property",
                    EpProperties.doubleEp(Labels.withId("latitude"), "latitude", Geo
                            .lat),
                    EpProperties.doubleEp(Labels.withId("longitude"), "longitude", Geo.lng)))
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
    json.put("testMeasurement", random.nextInt(100));
    json.put("testDimension", random.nextInt(10));
    json.put("string", getString(Arrays.asList("A", "B", "C")));
    json.put("string2", getString(Arrays.asList("A", "B", "C", "D")));
    json.put("integer", getInteger(0, 1, 1));
    json.put("integer2", getInteger(10, 100, 10));
    return json;
  }

  private Integer getInteger(int min, int max, int step) {
    return (Math.round(random.nextInt(max - min + 1) / step) * step) + min;
  }

  private String getString(List<String> strings) {
    return strings.get(random.nextInt(strings.size()));
  }

}

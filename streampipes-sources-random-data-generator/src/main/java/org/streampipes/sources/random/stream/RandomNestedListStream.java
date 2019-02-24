/*
Copyright 2019 FZI Forschungszentrum Informatik

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
package org.streampipes.sources.random.stream;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.sources.random.config.SourcesConfig;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;
import org.streampipes.vocabulary.Geo;

import java.util.Arrays;
import java.util.Random;

public class RandomNestedListStream extends RandomNumberStream {

  private static final String TOPIC = "org.streampipes.test.nested.list";
  private Random random;

  public RandomNestedListStream() {
    this.random = new Random();
  }

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("org.streampipes.random.number.list.nested", "Nested List " +
            "Random", "")
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    TOPIC))
            .format(Formats.jsonFormat())
            .property(EpProperties.timestampProperty("timestamp"))
            .property(EpProperties.listNestedEp(Labels.from("locations", "Locations", ""),
                    "locations", Arrays.asList(makeLatitude(), makeLongitude())))
            .property(EpProperties.nestedEp(Labels.from("nested1", "Nested", ""), "nested1",
                    EpProperties.listNestedEp(Labels.from("locations2", "Locations", ""),
                            "locations2", Arrays.asList(makeLatitude(), makeLongitude()))))
            .build();
  }

  private EventProperty makeLongitude() {
    return PrimitivePropertyBuilder.create(Datatypes.Double, "longitude")
            .label("Longitude")
            .domainProperty(Geo.lng)
            .build();
  }

  private EventProperty makeLatitude() {
    return PrimitivePropertyBuilder.create(Datatypes.Double, "latitude")
            .label("Latitude")
            .domainProperty(Geo.lat)
            .build();
  }

  @Override
  protected MessageResult getMessage(MessageConfig messageConfig) {
    try {
      return new MessageResult(
              buildJson(messageConfig.getTimestamp())
                      .toString()
                      .getBytes(), TOPIC);
    } catch (JSONException e) {
      e.printStackTrace();
      return new MessageResult(false);
    }
  }

  private JSONObject buildJson(long timestamp) throws JSONException {
    JSONObject json = new JSONObject();

    JSONObject nested = new JSONObject();
    nested.put("locations2", buildLocations());

    json.put("timestamp", timestamp);
    json.put("locations", buildLocations());
    json.put("nested1", nested);

    return json;
  }

  private JSONArray buildLocations() throws JSONException {
    JSONArray jsonArray = new JSONArray();
    for(int i = 0; i < 5; i++) {
      jsonArray.put(makeLocationObject());
    }
    return jsonArray;
  }

  private JSONObject makeLocationObject() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("latitude", random.nextDouble());
    jsonObject.put("longitude", random.nextDouble());
    return jsonObject;
  }
}

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

package org.streampipes.performance.dataprovider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonDataProvider implements DataProvider<String> {

  private EventSchema schema;
  private Long numberOfEvents;
  private List<String> outputEvents;
  private Random random;

  public JsonDataProvider(EventSchema schema, Long numberOfEvents) {
    this.schema = schema;
    this.numberOfEvents = numberOfEvents;
    this.outputEvents = new ArrayList<>();
    this.random = new Random();
  }

  @Override
  public List<String> getPreparedItems() {
    for (Long i = 0L; i < numberOfEvents; i++) {
      outputEvents.add(makeEvent());
    }

    return outputEvents;
  }

  private String makeEvent() {
    JsonObject jsonObject = new JsonObject();
    for (EventProperty property : schema.getEventProperties()) {
      jsonObject.add(property.getRuntimeName(), makeValue(property));
    }

    return jsonObject.toString();
  }

  private JsonElement makeValue(EventProperty property) {
    if (property instanceof EventPropertyPrimitive) {
//      switch (((EventPropertyPrimitive) property).getRuntimeType()) {
//        case XSD._integer.toString():
//          return new JsonPrimitive(getRandomInteger());
//        case XSD._string:
//          return new JsonPrimitive(getRandomString());
//        case XSD.LONG:
//          return new JsonPrimitive(getRandomLong());
//        case XSD.DOUBLE:
//          return new JsonPrimitive(getRandomDouble());
//        case XSD.FLOAT:
//          return new JsonPrimitive(getRandomFloat());
//      }
    } else if (property instanceof EventPropertyList) {
      // TODO
      return null;
    }
    // TODO: nested property
    return null;

  }


  private Float getRandomFloat() {
    return random.nextFloat();
  }

  private Double getRandomDouble() {
    return random.nextDouble();
  }

  private Long getRandomLong() {
    return random.nextLong();
  }

  private String getRandomString() {
    return RandomStringUtils.randomAlphabetic(20);
  }

  private Integer getRandomInteger() {
    return random.nextInt(1000);
  }

}

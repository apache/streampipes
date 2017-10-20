package org.streampipes.performance.dataprovider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;

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

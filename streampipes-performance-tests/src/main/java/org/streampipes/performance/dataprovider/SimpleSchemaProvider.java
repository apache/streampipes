package org.streampipes.performance.dataprovider;

import org.apache.commons.lang.RandomStringUtils;
import org.semarglproject.vocab.XSD;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimpleSchemaProvider {

  private static final List<String> runtimeTypes = Arrays.asList(XSD.INTEGER, XSD.LONG, XSD.STRING, XSD.FLOAT, XSD
          .DOUBLE);

  public EventSchema getSchema() {
    EventSchema schema = new EventSchema();
    List<EventProperty> properties = new ArrayList<>();
    for(int i = 0; i < 5; i++) {
      properties.add(makeRandomProperty());
    }

    schema.setEventProperties(properties);
    return schema;
  }

  private EventProperty makeRandomProperty() {
    EventPropertyPrimitive property = new EventPropertyPrimitive();
    property.setRuntimeName(RandomStringUtils.randomAlphabetic(5));
    property.setRuntimeType(runtimeTypes.get(new Random().nextInt(5)));

    return property;
  }
}

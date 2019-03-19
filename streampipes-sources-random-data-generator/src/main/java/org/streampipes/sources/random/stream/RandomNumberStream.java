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

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.quality.Accuracy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;
import org.streampipes.sources.random.util.MessageProducer;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class RandomNumberStream implements DataStreamDeclarer {

  protected Random random;

  public RandomNumberStream() {
    this.random = new Random();
  }


  protected List<EventProperty> getEventPropertyDescriptions() {

    List<EventPropertyQualityDefinition> randomValueQualities = new ArrayList<>();
    randomValueQualities.add(new Accuracy((float) 0.5));

    List<EventProperty> eventProperties = new ArrayList<>();
    eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
            Utils.createURI("http://schema.org/DateTime")));


    EventPropertyPrimitive randomValue = new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "",
            Utils.createURI("http://schema.org/Number"));
    randomValue.setMeasurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeFahrenheit"));
    eventProperties.add(randomValue);


    eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "randomString", "",
            Utils.createURI(SO.Text)));


    EventPropertyPrimitive property = new EventPropertyPrimitive(XSD._long.toString(), "count", "",
            Utils.createURI("http://schema.org/Number"));
    property.setMeasurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeCelsius"));
    eventProperties.add(property);

    return eventProperties;
  }

  @Override
  public void executeStream() {
    Runnable r = new MessageProducer(this::getMessage);
    Thread thread = new Thread(r);
    thread.start();
  }

  @Override
  public boolean isExecutable() {
    return true;
  }

  protected String randomString() {
    String[] randomStrings = new String[]{"a", "b", "c", "d"};
    Random random = new Random();
    return randomStrings[random.nextInt(3)];
  }

  protected abstract MessageResult getMessage(MessageConfig messageConfig);

}

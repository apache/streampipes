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

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.quality.Accuracy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.sources.config.SourcesConfig;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class RandomNumberStream implements EventStreamDeclarer {

  SpKafkaProducer kafkaProducer;
  private String topic;

  final static long SIMULATION_DELAY_MS = SourcesConfig.INSTANCE.getSimulaitonDelayMs();
  final static int SIMULATION_DELAY_NS = SourcesConfig.INSTANCE.getSimulaitonDelayNs();

  public RandomNumberStream() {

  }

  public RandomNumberStream(String topic) {
    this.topic = topic;
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

    kafkaProducer = getKafkaProducer(topic);

    Runnable r = new Runnable() {

      @Override
      public void run() {
        Random random = new Random();
        int j = 0;
        for (int i = 0; i < SourcesConfig.INSTANCE.getMaxEvents(); i++) {
          try {
            if (j % 50 == 0) {
              System.out.println(j + " Events (Random Number) sent.");
            }
            Optional<byte[]> nextMsg = getMessage(System.currentTimeMillis(), random.nextInt(100), j);
            nextMsg.ifPresent(bytes -> kafkaProducer.publish(bytes));
            Thread.sleep(SIMULATION_DELAY_MS, SIMULATION_DELAY_NS);
            if (j % SourcesConfig.INSTANCE.getSimulationWaitEvery() == 0) {
              Thread.sleep(SourcesConfig.INSTANCE.getSimulationWaitFor());
            }
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

  @Override
  public boolean isExecutable() {
    return true;
  }

  protected String randomString() {
    String[] randomStrings = new String[]{"a", "b", "c", "d"};
    Random random = new Random();
    return randomStrings[random.nextInt(3)];
  }

  protected abstract Optional<byte[]> getMessage(long nanoTime, int randomNumber, int counter);

  public SpKafkaProducer getKafkaProducer(String topic) {
    return new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), topic);
  }
}

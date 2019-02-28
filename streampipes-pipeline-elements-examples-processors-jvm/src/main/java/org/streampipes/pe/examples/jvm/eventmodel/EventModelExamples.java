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
package org.streampipes.pe.examples.jvm.eventmodel;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.runtime.Event;
import org.streampipes.pe.examples.jvm.base.DummyParameters;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class EventModelExamples implements EventProcessor<DummyParameters> {

  /**
   * Example event:
   * { "timestamp" : 12345, "temperature" : 45.6, "deviceId" : "sensor1", "running" : true,
   * "location" : {"latitude" : 34.4, "longitude" : -47}, "lastValues" : [45, 22, 21]}
   */

  private String temperatureSelector;
  private String runningSelector;
  private String deviceIdSelector;
  private String latitudeSelector;
  private String lastValueSelector;

  @Override
  public void onInvocation(DummyParameters parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    // usually, the fields such as temperatureSelector would be retrieved from the parameter class
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    Float temperatureValue = event.getFieldBySelector(temperatureSelector).getAsPrimitive().getAsFloat();
    String deviceId = event.getFieldBySelector(deviceIdSelector).getAsPrimitive().getAsString();

    Double latitude = event.getFieldBySelector(latitudeSelector).getAsPrimitive().getAsDouble();

    Boolean running = event.getFieldBySelector(runningSelector).getAsPrimitive().getAsBoolean();

    List<Integer> lastValues = event.getFieldBySelector(lastValueSelector).getAsList().parseAsSimpleType(Integer.class);


    event.addField("city", "Karlsruhe");
    event.removeFieldBySelector(temperatureSelector);
    event.addField("fahrenheit", 48);

  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}

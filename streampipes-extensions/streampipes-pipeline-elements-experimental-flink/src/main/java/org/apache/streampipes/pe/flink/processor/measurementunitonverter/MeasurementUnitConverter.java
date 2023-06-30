/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.pe.flink.processor.measurementunitonverter;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.units.UnitProvider;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class MeasurementUnitConverter implements FlatMapFunction<Event, Event> {

  private String convertProperty;
  private String inputUnitUri;
  private String outputUnitUri;

  public MeasurementUnitConverter(String inputUnitUri, String
      outputUnitUri, String convertProperty) {
    this.convertProperty = convertProperty;

    this.inputUnitUri = inputUnitUri;
    this.outputUnitUri = outputUnitUri;
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws Exception {
    Unit inputUnit = UnitProvider.INSTANCE.getUnit(inputUnitUri);
    Unit outputUnit = UnitProvider.INSTANCE.getUnit(outputUnitUri);
    Double value = (double) in.getFieldBySelector(convertProperty).getAsPrimitive()
        .getAsDouble();

    // transform old value to new unit
    Quantity obs = new Quantity(value, inputUnit);
    Double newValue = obs.convertTo(outputUnit).getValue();

    in.updateFieldBySelector(convertProperty, newValue);

    out.collect(in);
  }
}

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

package org.apache.streampipes.connect.management.compact.generator;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

public class AdapterTransformationRuleGenerator implements AdapterModelGenerator {

  @Override
  public void apply(AdapterDescription adapterDescription,
                    CompactAdapter compactAdapter) throws Exception {
    if (compactAdapter.transform() != null) {
      var transforms = compactAdapter.transform();
      if (transforms.rename() != null) {
        transforms.rename().forEach((key, value) -> {
          addRule(adapterDescription, new RenameRuleDescription(key, value));
          findProperty(adapterDescription, key).forEach(p -> p.setRuntimeName(value));
        });
      }
      if (transforms.measurementUnit() != null) {
        transforms.measurementUnit().forEach((key, value) -> {
          var currentUnitOpt = getCurrentUnit(adapterDescription, key);
          currentUnitOpt.ifPresent(unit -> {
            addRule(adapterDescription, new UnitTransformRuleDescription(key, unit, value));
            findProperty(adapterDescription, key)
                .filter(ep -> ep instanceof EventPropertyPrimitive)
                .map(ep -> (EventPropertyPrimitive) ep)
                .forEach(ep -> ep.setMeasurementUnit(URI.create(value)));
          });
        });
      }
    }
  }

  private void addRule(AdapterDescription adapterDescription,
                       TransformationRuleDescription rule) {
    adapterDescription.getRules().add(rule);
  }

  private Optional<String> getCurrentUnit(AdapterDescription adapterDescription,
                                          String runtimeName) {
    return findProperty(adapterDescription, runtimeName)
        .filter(ep -> ep instanceof EventPropertyPrimitive)
        .map(ep -> (EventPropertyPrimitive) ep)
        .map(EventPropertyPrimitive::getMeasurementUnit)
        .map(URI::toString)
        .findFirst();
  }

  private Stream<EventProperty> findProperty(AdapterDescription adapterDescription,
                                             String runtimeName) {
    return adapterDescription
        .getDataStream()
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(ep -> ep.getRuntimeName().equalsIgnoreCase(runtimeName));
  }
}

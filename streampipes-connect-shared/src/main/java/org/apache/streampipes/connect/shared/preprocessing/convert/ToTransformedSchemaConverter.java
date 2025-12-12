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

package org.apache.streampipes.connect.shared.preprocessing.convert;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.util.Cloner;

import java.net.URI;
import java.util.List;

import static org.apache.streampipes.connect.shared.preprocessing.utils.ConversionUtils.findPrimitiveProperty;

public class ToTransformedSchemaConverter implements ITransformationRuleVisitor, ProvidesConversionResult {

  private static final String TIMESTAMP_ID_PREFIX = "http://eventProperty.de/timestamp/";
  private static final String STATIC_VALUE_ID_PREFIX = "http://eventProperty.de/staticValue/";

  private final List<EventProperty> properties;

  public ToTransformedSchemaConverter(List<EventProperty> properties) {
    this.properties = new Cloner().properties(properties);
  }

  @Override
  public void visit(EventRateTransformationRuleDescription rule) {
    // does not affect schema
  }

  @Override
  public void visit(RemoveDuplicatesTransformationRuleDescription rule) {
    // does not affect schema
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription rule) {
    var property = findPrimitiveProperty(properties, rule.getRuntimeKey());
    property.setRuntimeType(rule.getTargetDatatypeXsd());
  }

  @Override
  public void visit(UnitTransformRuleDescription rule) {
    var property = findPrimitiveProperty(properties, rule.getRuntimeKey());
    property.setMeasurementUnit(URI.create(rule.getToUnitRessourceURL()));
    var metadata = property.getAdditionalMetadata();
    metadata.put("fromMeasurementUnit", rule.getFromUnitRessourceURL());
    metadata.put("toMeasurementUnit", rule.getToUnitRessourceURL());
  }

  @Override
  public List<EventProperty> getTransformedProperties() {
    return this.properties;
  }
}

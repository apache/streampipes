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

import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.util.List;

import static org.apache.streampipes.connect.shared.preprocessing.utils.ConversionUtils.findPrimitiveProperty;
import static org.apache.streampipes.connect.shared.preprocessing.utils.ConversionUtils.findProperty;
import static org.apache.streampipes.connect.shared.preprocessing.utils.ConversionUtils.findPropertyHierarchy;

public class ToTransformedSchemaConverter implements ITransformationRuleVisitor, ProvidesConversionResult {

  private final List<EventProperty> properties;

  public ToTransformedSchemaConverter(List<EventProperty> properties) {
    this.properties = new Cloner().properties(properties);
  }

  @Override
  public void visit(CreateNestedRuleDescription rule) {
    var nested = new EventPropertyNested();
    nested.setRuntimeName(rule.getRuntimeKey());
    this.properties.add(nested);
  }

  @Override
  public void visit(DeleteRuleDescription rule) {
    var keyArray = Utils.toKeyArray(rule.getRuntimeKey());
    var properties = findPropertyHierarchy(this.properties, rule.getRuntimeKey());
    properties.removeIf(property -> property.getRuntimeName().equals(keyArray.get(0)));
  }

  @Override
  public void visit(MoveRuleDescription rule) {
    var existing = new Cloner().property(findProperty(properties, rule.getOldRuntimeKey()));
    var existingHierarchy = findPropertyHierarchy(this.properties, rule.getOldRuntimeKey());
    existingHierarchy.removeIf(property -> property.getRuntimeName().equals(existing.getRuntimeName()));
    try {
      var targetProperty = findProperty(this.properties, Utils.toKeyArray(rule.getNewRuntimeKey()));
      if (targetProperty instanceof EventPropertyNested) {
        ((EventPropertyNested) targetProperty).getEventProperties().add(existing);
      }
    } catch (IllegalArgumentException e) {
      this.properties.add(existing);
    }
  }

  @Override
  public void visit(RenameRuleDescription rule) {
    var property = findProperty(properties, rule.getOldRuntimeKey());
    property.setRuntimeName(rule.getNewRuntimeKey());
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
  public void visit(AddTimestampRuleDescription rule) {
    this.properties.add(EpProperties.timestampProperty(rule.getRuntimeKey()));
  }

  @Override
  public void visit(AddValueTransformationRuleDescription rule) {
    this.properties.add(EpProperties.stringEp(Labels.empty(), rule.getRuntimeKey(), ""));
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription rule) {
    var property = findPrimitiveProperty(properties, rule.getRuntimeKey());
    property.setRuntimeType(rule.getTargetDatatypeXsd());
  }

  @Override
  public void visit(CorrectionValueTransformationRuleDescription rule) {
    // does not affect schema
  }

  @Override
  public void visit(TimestampTranfsformationRuleDescription rule) {
    var property = findPrimitiveProperty(properties, rule.getRuntimeKey());
    property.setDomainProperties(List.of(URI.create("http://schema.org/DateTime")));
    property.setRuntimeType(Datatypes.Long.toString());
  }

  @Override
  public void visit(UnitTransformRuleDescription rule) {
    var property = findPrimitiveProperty(properties, rule.getRuntimeKey());
    property.setMeasurementUnit(URI.create(rule.getToUnitRessourceURL()));
  }

  @Override
  public List<EventProperty> getTransformedProperties() {
    return this.properties;
  }
}

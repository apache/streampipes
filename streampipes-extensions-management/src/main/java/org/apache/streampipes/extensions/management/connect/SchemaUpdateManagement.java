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
package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.connect.shared.preprocessing.convert.SchemaConverter;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;
import java.util.Optional;

public class SchemaUpdateManagement {

  public void computeSchemaChanges(AdapterDescription adapterDescription, GuessSchema schema) {
    var actualFields = schema.getEventSchema().getEventProperties();
    var previousFields = adapterDescription.getEventSchema().getEventProperties();
    var transformationRules = adapterDescription.getRules();

    var removedFields = findRemovedFields(actualFields, previousFields);
    var currentOriginalSchema = new SchemaConverter().toOriginalSchema(adapterDescription.getEventSchema(),
            transformationRules);
    var guessedOriginalSchema = schema.getEventSchema();
    var modifiedSchema = modifySchema(currentOriginalSchema, guessedOriginalSchema);
    var modifiedRules = modifyTransformationRules(transformationRules, modifiedSchema);
    var transformedSchema = new SchemaConverter().toTransformedSchema(modifiedSchema, modifiedRules);
    schema.setEventSchema(modifiedSchema);
    schema.setTargetSchema(transformedSchema);

    schema.setRemovedProperties(removedFields);
  }

  private List<TransformationRuleDescription> modifyTransformationRules(List<TransformationRuleDescription> rules,
          EventSchema modifiedSchema) {
    var properties = modifiedSchema.getEventProperties();
    var visitor = new TransformationRuleUpdateVisitor(properties, rules);
    rules.forEach(rule -> rule.accept(visitor));
    return visitor.getValidRules();
  }

  private EventSchema modifySchema(EventSchema currentOriginalSchema, EventSchema guessedOriginalSchema) {
    return new EventSchema(guessedOriginalSchema.getEventProperties().stream().map(ep -> {
      var currentEpOpt = getExistingEp(ep, currentOriginalSchema);
      return currentEpOpt.orElse(ep);
    }).toList());
  }

  private Optional<EventProperty> getExistingEp(EventProperty ep, EventSchema currentOriginalSchema) {
    return currentOriginalSchema.getEventProperties().stream().filter(currEp -> epExists(ep, currEp)).findFirst();
  }

  private boolean epExists(EventProperty ep, EventProperty currEp) {
    return ep.getClass().equals(currEp.getClass()) && ep.getRuntimeName().equals(currEp.getRuntimeName())
            && isSameDatatype(ep, currEp);
  }

  private boolean isSameDatatype(EventProperty ep, EventProperty currEp) {
    if (ep instanceof EventPropertyPrimitive && currEp instanceof EventPropertyPrimitive) {
      return ((EventPropertyPrimitive) ep).getRuntimeType().equals(((EventPropertyPrimitive) currEp).getRuntimeType());
    } else {
      return true;
    }
  }

  private List<EventProperty> findRemovedFields(List<EventProperty> actualFields, List<EventProperty> previousFields) {
    return previousFields.stream().filter(field -> !existsField(field, actualFields)).toList();
  }

  private boolean existsField(EventProperty field, List<EventProperty> actualFields) {
    return actualFields.stream().anyMatch(currField -> field.getRuntimeName().equals(currField.getRuntimeName()));
  }
}

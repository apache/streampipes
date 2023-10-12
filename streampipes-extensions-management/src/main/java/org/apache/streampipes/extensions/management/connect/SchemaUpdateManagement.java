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

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

public class SchemaUpdateManagement {

  public void computeSchemaChanges(AdapterDescription adapterDescription,
                                   GuessSchema schema) {
    var actualFields = schema.getEventSchema().getEventProperties();
    var previousFields = adapterDescription.getEventSchema().getEventProperties();
    var transformationRules = adapterDescription.getSchemaRules();

    var removedFields = findRemovedFields(actualFields, previousFields);
    var modifiedFieldNames = findModifiedFields(actualFields, previousFields);


    schema.setRemovedProperties(removedFields);
    //schema.setUpdateNotifications();
  }

  private List<EventProperty> findRemovedFields(List<EventProperty> actualFields,
                                                List<EventProperty> previousFields) {
    return previousFields
        .stream()
        .filter(field -> !existsField(field, actualFields))
        .toList();
  }

  private List<EventProperty> findModifiedFields(List<EventProperty> actualFields,
                                                List<EventProperty> previousFields) {
    return List.of();
  }

  private boolean existsField(EventProperty field,
                              List<EventProperty> actualFields) {
    return actualFields
        .stream()
        .noneMatch(currField -> field.getRuntimeName().equals(currField.getRuntimeName()));
  }
}

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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;

import java.util.Objects;
import java.util.Optional;

/** Selects a representative stored field for approximate metadata queries. */
public final class DatasetQueryFields {
  private DatasetQueryFields() {
  }

  public static Optional<String> firstCountableProperty(DatasetMetadata dataset) {
    if (dataset.getEventSchema() == null || dataset.getEventSchema().getEventProperties() == null) {
      return Optional.empty();
    }
    return dataset.getEventSchema().getEventProperties().stream()
        .filter(Objects::nonNull)
        .filter(property -> !(property instanceof EventPropertyPrimitive)
            || !PropertyScope.DIMENSION_PROPERTY.name().equals(property.getPropertyScope()))
        .map(EventProperty::getRuntimeName)
        .filter(name -> name != null && !name.isBlank() && !name.equals("*"))
        .findFirst();
  }
}

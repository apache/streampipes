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

package org.apache.streampipes.connect.management.compact;

import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;

import java.net.URI;
import java.util.Map;

public class SchemaMetadataEnricher {

  public void enrich(
      EventProperty property,
      CompactEventProperty propertyDef
  ) {
    if (propertyDef.propertyScope() != null) {
      property.setPropertyScope(convertScope(propertyDef.propertyScope()).name());
    }
    if (propertyDef.label() != null) {
      property.setLabel(propertyDef.label());
    }
    if (propertyDef.description() != null) {
      property.setDescription(propertyDef.description());
    }
    if (property instanceof EventPropertyPrimitive && propertyDef.semanticType() != null) {
      property.setSemanticType(propertyDef.semanticType());
    }

    if (propertyDef.additionalMetadata() != null
        && propertyDef.additionalMetadata()
                      .containsKey("fromMeasurementUnit")
        && propertyDef.additionalMetadata()
                      .get(
                          "toMeasurementUnit") != null) {
      String toUnit = propertyDef.additionalMetadata()
                                 .get("toMeasurementUnit")
                                 .toString();
      String fromUnit = propertyDef.additionalMetadata()
                                   .get("fromMeasurementUnit")
                                   .toString();
      ((EventPropertyPrimitive) property).setMeasurementUnit(URI.create(toUnit));

      property.setAdditionalMetadata(Map.of(
          "fromMeasurementUnit", fromUnit,
          "toMeasurementUnit", toUnit
      ));
    }
  }

  private PropertyScope convertScope(String scope) {
    if (scope == null || scope.isBlank()) {
      return PropertyScope.MEASUREMENT_PROPERTY;
    }

    return switch (scope.trim().toUpperCase()) {
      case "HEADER", "HEADER_PROPERTY" -> PropertyScope.HEADER_PROPERTY;
      case "DIMENSION", "DIMENSION_PROPERTY" -> PropertyScope.DIMENSION_PROPERTY;
      case "MEASUREMENT", "MEASUREMENT_PROPERTY" -> PropertyScope.MEASUREMENT_PROPERTY;
      case "NONE" -> PropertyScope.NONE;
      default -> PropertyScope.MEASUREMENT_PROPERTY;
    };
  }

}

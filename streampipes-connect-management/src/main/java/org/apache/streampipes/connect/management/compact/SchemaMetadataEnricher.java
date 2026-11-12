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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.units.UnitConverter;
import org.apache.streampipes.units.catalog.UnitCatalog;
import org.apache.streampipes.units.model.MeasurementUnit;

import java.util.Map;

public class SchemaMetadataEnricher {

  private final UnitCatalog unitCatalog;

  public SchemaMetadataEnricher() {
    this(UnitCatalog.builtIn());
  }

  public SchemaMetadataEnricher(UnitCatalog unitCatalog) {
    this.unitCatalog = unitCatalog;
  }

  /**
   * Applies the metadata of a compact adapter definition to an event property.
   *
   * <p>Measurement units may be given by any identifier the unit catalog knows (e.g.
   * {@code DEG_C}); they are stored with their canonical identifier.</p>
   *
   * @throws AdapterException if a measurement unit is unknown or the two units of a unit
   *                          conversion measure different quantities
   */
  public void enrich(
      EventProperty property,
      CompactEventProperty propertyDef
  ) throws AdapterException {
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
      var fromUnit = resolveUnit(propertyDef.additionalMetadata().get("fromMeasurementUnit"), property);
      var toUnit = resolveUnit(propertyDef.additionalMetadata().get("toMeasurementUnit"), property);
      if (!UnitConverter.isConvertible(fromUnit, toUnit)) {
        throw new AdapterException(
            "Cannot convert property '%s' from %s to %s: the units measure different quantities".formatted(
                property.getRuntimeName(), fromUnit.label(), toUnit.label()));
      }
      ((EventPropertyPrimitive) property).setMeasurementUnit(toUnit.resource());

      property.setAdditionalMetadata(Map.of(
          "fromMeasurementUnit", fromUnit.resource().toString(),
          "toMeasurementUnit", toUnit.resource().toString()
      ));
    }
  }

  private MeasurementUnit resolveUnit(Object unitId, EventProperty property) throws AdapterException {
    var id = unitId == null ? null : unitId.toString();
    var unit = unitCatalog.find(id);
    if (unit.isEmpty()) {
      throw new AdapterException(
          "Unknown measurement unit '%s' for property '%s'".formatted(id, property.getRuntimeName()));
    }
    return unit.get();
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

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
import org.apache.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.List;

public class SchemaMetadataEnricher {

  public void enrich(EventProperty property,
                     CompactEventProperty propertyDef) {
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
      property.setDomainProperties(List.of(URI.create(propertyDef.semanticType())));
    }
  }

  private PropertyScope convertScope(String scope) {
    if (scope.equalsIgnoreCase("header")) {
      return PropertyScope.HEADER_PROPERTY;
    } else if (scope.equalsIgnoreCase("dimension")) {
      return PropertyScope.DIMENSION_PROPERTY;
    } else if (scope.equalsIgnoreCase("measurement")) {
      return PropertyScope.MEASUREMENT_PROPERTY;
    } else {
      return PropertyScope.NONE;
    }
  }

  private String convertType(String shortRuntimeType) {
    return switch (shortRuntimeType) {
      case "integer" -> XSD.INTEGER.toString();
      case "boolean" -> XSD.BOOLEAN.toString();
      case "float" -> XSD.FLOAT.toString();
      case "double" -> XSD.DOUBLE.toString();
      default -> XSD.STRING.toString();
    };
  }
}

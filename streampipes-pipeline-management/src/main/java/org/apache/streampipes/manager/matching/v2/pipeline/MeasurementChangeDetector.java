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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MeasurementChangeDetector {

  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";

  public boolean hasCriticalMeasurementFieldChange(Pipeline pipeline,
                                                   String affectedElementId,
                                                   EventSchema updatedEventSchema) {
    if (!hasDatabaseSink(pipeline)) {
      return false;
    }

    return getEventSchema(pipeline, affectedElementId)
        .map(existingEventSchema ->
            hasCriticalMeasurementFieldChange(existingEventSchema, updatedEventSchema))
        .orElse(false);
  }

  private boolean hasDatabaseSink(Pipeline pipeline) {
    return streamOf(pipeline.getActions())
        .anyMatch(this::isDatabaseSink);
  }

  public boolean isDatabaseSink(DataSinkInvocation dataSink) {
    return DATA_LAKE_SINK_APP_ID.equals(dataSink.getAppId())
        || streamOf(dataSink.getCategory()).anyMatch(DataSinkType.DATABASE.name()::equals);
  }

  private Optional<EventSchema> getEventSchema(Pipeline pipeline,
                                               String affectedElementId) {
    return pipeline
        .getStreams()
        .stream()
        .filter(stream -> stream.getElementId().equals(affectedElementId))
        .findFirst()
        .map(SpDataStream::getEventSchema);
  }

  public boolean hasCriticalMeasurementFieldChange(EventSchema existingEventSchema,
                                                   EventSchema updatedEventSchema) {
    var existingMeasurementFields = existingEventSchema
        .getEventProperties()
        .stream()
        .filter(this::isMeasurementField)
        .collect(Collectors.toMap(
            EventProperty::getRuntimeName,
            Function.identity(),
            (existingProperty, duplicateProperty) -> existingProperty
        ));

    return updatedEventSchema
        .getEventProperties()
        .stream()
        .filter(this::isMeasurementField)
        .anyMatch(updatedProperty -> {
          var existingProperty = existingMeasurementFields.get(updatedProperty.getRuntimeName());
          return existingProperty != null && hasCriticalFieldTypeChange(existingProperty, updatedProperty);
        });
  }

  private boolean isMeasurementField(EventProperty eventProperty) {
    return !PropertyScope.DIMENSION_PROPERTY.name().equals(eventProperty.getPropertyScope());
  }

  private boolean hasCriticalFieldTypeChange(EventProperty existingProperty,
                                             EventProperty updatedProperty) {
    return toStorageType(existingProperty) != toStorageType(updatedProperty);
  }

  private StorageType toStorageType(EventProperty eventProperty) {
    if (eventProperty instanceof EventPropertyPrimitive primitiveProperty) {
      return toPrimitiveStorageType(primitiveProperty.getRuntimeType());
    } else {
      return StorageType.STRING;
    }
  }

  private StorageType toPrimitiveStorageType(String runtimeType) {
    if (XSD.INTEGER.toString().equals(runtimeType)
        || XSD.LONG.toString().equals(runtimeType)) {
      return StorageType.INTEGER;
    } else if (XSD.FLOAT.toString().equals(runtimeType)
        || XSD.DOUBLE.toString().equals(runtimeType)
        || SO.NUMBER.equals(runtimeType)) {
      return StorageType.FLOAT;
    } else if (XSD.BOOLEAN.toString().equals(runtimeType)) {
      return StorageType.BOOLEAN;
    } else {
      return StorageType.STRING;
    }
  }

  private enum StorageType {
    INTEGER,
    FLOAT,
    BOOLEAN,
    STRING
  }

  private <T> Stream<T> streamOf(Iterable<T> iterable) {
    if (iterable == null) {
      return Stream.empty();
    }

    return StreamSupport.stream(iterable.spliterator(), false);
  }
}

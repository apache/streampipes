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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class MeasurementUpdateUtils {

  public static final String DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";
  public static final String DATASET_MEASUREMENT_FIELD = "db_measurement";

  private MeasurementUpdateUtils() {
  }

  public static List<DataSinkInvocation> getDatasetSinks(Pipeline pipeline) {
    return pipeline.getActions()
        .stream()
        .filter(MeasurementUpdateUtils::isDatasetSink)
        .toList();
  }

  public static Optional<DataSinkInvocation> getDatasetSinkById(Pipeline pipeline, String id) {
    return pipeline.getActions()
        .stream()
        .filter(MeasurementUpdateUtils::isDatasetSink)
        .filter(storedSink -> Objects.equals(storedSink.getElementId(), id))
        .findFirst();
  }

  public static Set<String> extractMeasureNames(Pipeline pipeline) {
    return getDatasetSinks(pipeline)
        .stream()
        .map(MeasurementUpdateUtils::extractMeasureName)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  public static Optional<String> extractMeasureName(DataSinkInvocation sink) {
    return Optional
        .ofNullable(sink.getStaticProperties())
        .stream()
        .flatMap(List::stream)
        .filter(property -> DATASET_MEASUREMENT_FIELD.equals(property.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .filter(Objects::nonNull)
        .findFirst();
  }

  public static boolean isDatasetSink(DataSinkInvocation dataSink) {
    return DATASET_SINK_APP_ID.equals(dataSink.getAppId());
  }
}

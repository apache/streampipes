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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MeasurementChangeValidationStep extends AbstractPipelineValidationStep {

  public static final String MEASUREMENT_UPDATE_REQUIRED =
      "Measurement field storage type changed. Manual measurement update handling required";
  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";

  @Override
  public void apply(NamedStreamPipesEntity source,
                    InvocableStreamPipesEntity target,
                    Set<InvocableStreamPipesEntity> allTargets,
                    List<PipelineElementValidationInfo> validationInfos) {
    if (target instanceof DataSinkInvocation dataSink && isDatabaseSink(dataSink)) {
      var criticalFieldChanges = findCriticalMeasurementFieldChanges(source, dataSink);
      if (!criticalFieldChanges.isEmpty()) {
        validationInfos.add(PipelineElementValidationInfo.error(makeValidationMessage(criticalFieldChanges)));
      }
    }

    if (target.getInputStreams() != null && target.getInputStreams().size() > 1) {
      this.visitorHistory.put(target.getDom(), 1);
    }
  }

  private boolean isDatabaseSink(DataSinkInvocation dataSink) {
    return DATA_LAKE_SINK_APP_ID.equals(dataSink.getAppId())
        || streamOf(dataSink.getCategory()).anyMatch(DataSinkType.DATABASE.name()::equals);
  }

  private List<CriticalMeasurementFieldChange> findCriticalMeasurementFieldChanges(
      NamedStreamPipesEntity source,
      DataSinkInvocation dataSink) {
    var existingEventSchema = getExistingDataSinkSchema(dataSink);
    var updatedEventSchema = getUpdatedSourceSchema(source);

    if (existingEventSchema.isPresent() && updatedEventSchema.isPresent()) {
      return MeasurementChangeDetector.findCriticalMeasurementFieldChanges(
          existingEventSchema.get(),
          updatedEventSchema.get()
      );
    } else {
      return List.of();
    }
  }

  private String makeValidationMessage(
      List<CriticalMeasurementFieldChange> criticalFieldChanges) {
    return MEASUREMENT_UPDATE_REQUIRED + ": " + criticalFieldChanges
        .stream()
        .map(change -> "%s (%s -> %s)".formatted(
            change.runtimeName(),
            change.existingType(),
            change.updatedType()
        ))
        .collect(Collectors.joining(", "));
  }

  private Optional<EventSchema> getExistingDataSinkSchema(DataSinkInvocation dataSink) {
    if (dataSink.getInputStreams() == null || dataSink.getInputStreams().size() <= getIndex(dataSink)) {
      return Optional.empty();
    }

    return Optional.ofNullable(dataSink.getInputStreams().get(getIndex(dataSink)).getEventSchema());
  }

  private Optional<EventSchema> getUpdatedSourceSchema(NamedStreamPipesEntity source) {
    if (source instanceof SpDataStream dataStream) {
      return Optional.ofNullable(dataStream.getEventSchema());
    } else if (source instanceof DataProcessorInvocation dataProcessor
        && dataProcessor.getOutputStream() != null) {
      return Optional.ofNullable(dataProcessor.getOutputStream().getEventSchema());
    } else {
      return Optional.empty();
    }
  }

  private <T> Stream<T> streamOf(Iterable<T> iterable) {
    if (iterable == null) {
      return Stream.empty();
    }

    return StreamSupport.stream(iterable.spliterator(), false);
  }
}

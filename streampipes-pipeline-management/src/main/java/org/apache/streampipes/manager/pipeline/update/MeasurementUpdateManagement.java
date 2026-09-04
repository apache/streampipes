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

import org.apache.streampipes.manager.matching.v2.pipeline.MeasurementChangeDetector;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.ChartSchemaUpdateInfo;
import org.apache.streampipes.model.datalake.CriticalMeasurementFieldChange;
import org.apache.streampipes.model.datalake.MeasurementUpdateInfo;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MeasurementUpdateManagement {

  private final ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator;
  private final IPipelineStorage pipelineStorage;

  public MeasurementUpdateManagement(IPipelineStorage pipelineStorage,
                                     ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator) {
    this.chartSchemaUpdateCoordinator = chartSchemaUpdateCoordinator;
    this.pipelineStorage = pipelineStorage;
  }

  public List<MeasurementUpdateInfo> checkPipelineMigrations(String pipelineId,
                                                             Pipeline updatedPipeline) {
    var storedPipeline = pipelineStorage.getElementById(pipelineId);

    return checkPipelineMigrations(storedPipeline, updatedPipeline);
  }

  List<MeasurementUpdateInfo> checkPipelineMigrations(Pipeline storedPipeline,
                                                      Pipeline updatedPipeline) {
    return MeasurementUpdateUtils.getDataLakeSinks(updatedPipeline)
        .stream()
        .map(updatedSink -> makeUpdateInfo(storedPipeline, updatedSink))
        .flatMap(Optional::stream)
        .toList();
  }

  private Optional<MeasurementUpdateInfo> makeUpdateInfo(Pipeline storedPipeline,
                                                         DataSinkInvocation updatedSink) {
    var criticalFieldChanges = findCriticalMeasurementFieldChanges(storedPipeline, updatedSink);
    var chartSchemaUpdateInfos = findChartSchemaUpdateInfos(updatedSink);

    if (criticalFieldChanges.isEmpty() && chartSchemaUpdateInfos.isEmpty()) {
      return Optional.empty();
    } else {
      var measurementUpdateInfo = new MeasurementUpdateInfo();
      measurementUpdateInfo.setMeasurementName(MeasurementUpdateUtils.extractMeasureName(updatedSink).orElse(null));
      measurementUpdateInfo.setCriticalMeasurementFieldChanges(criticalFieldChanges);
      measurementUpdateInfo.setChartSchemaUpdateInfos(chartSchemaUpdateInfos);
      return Optional.of(measurementUpdateInfo);
    }
  }

  private List<CriticalMeasurementFieldChange> findCriticalMeasurementFieldChanges(Pipeline storedPipeline,
                                                                                   DataSinkInvocation updatedSink) {
    return MeasurementUpdateUtils.getDataLakeSinkById(storedPipeline, updatedSink.getElementId())
        .flatMap(storedSink -> getFirstInputStreamSchema(storedSink)
            .flatMap(existingSchema -> getFirstInputStreamSchema(updatedSink)
                .map(updatedSchema -> MeasurementChangeDetector.findCriticalMeasurementFieldChanges(
                    existingSchema,
                    updatedSchema
                ))))
      .orElse(List.of());
  }

  private List<ChartSchemaUpdateInfo> findChartSchemaUpdateInfos(DataSinkInvocation updatedSink) {
    return getFirstInputStreamSchema(updatedSink)
        .map(updatedSchema -> chartSchemaUpdateCoordinator.checkChartMigrations(
            MeasurementUpdateUtils.extractMeasureName(updatedSink).stream().collect(Collectors.toSet()),
            updatedSchema
        ))
        .orElse(List.of());
  }

  private Optional<EventSchema> getFirstInputStreamSchema(DataSinkInvocation sink) {
    return Optional
        .ofNullable(sink.getInputStreams())
        .stream()
        .flatMap(List::stream)
        .findFirst()
        .map(SpDataStream::getEventSchema);
  }

}

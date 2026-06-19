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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DatasetSummaryDto;
import org.apache.streampipes.model.datalake.RetentionTimeConfig;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataLakeMeasureResourceManager extends AbstractResourceManager<IDataLakeMeasureStorage> {

  private static final String DATA_LAKE_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String MEASURE_FIELD_INTERNAL_NAME = "db_measurement";
  private static final List<String> FORBIDDEN_MEASURE_NAME_CHARS = List.of("/", "?", "=", "\"");

  private final IPipelineStorage pipelineStorage;
  private final SpPermissionEvaluator permissionEvaluator;

  public DataLakeMeasureResourceManager(IDataLakeMeasureStorage datasetStorage,
                                        IPipelineStorage pipelineStorage,
                                        PermissionResourceManager permissionResourceManager) {
    super(datasetStorage);
    this.pipelineStorage = pipelineStorage;
    this.permissionEvaluator = new SpPermissionEvaluator(permissionResourceManager.getDb());
  }

  public ResourceSummaryDto<DatasetSummaryDto> getSummary(Authentication auth) {
    var pipelinesByMeasure = getVisiblePipelinesByMeasure(auth);

    var datasets = db.findAll().stream()
        .filter(measure -> canReadMeasure(auth, measure))
        .map(measure -> toSummary(measure, pipelinesByMeasure))
        .toList();

    return new ResourceSummaryDto<>(datasets, datasets.size());
  }

  private DatasetSummaryDto toSummary(
      DataLakeMeasure measure,
      Map<String, List<PipelineInfo>> pipelinesByMeasure
  ) {
    var pipelineInfos = pipelinesByMeasure.getOrDefault(measure.getMeasureName(), List.of());
    var pipelines = pipelineInfos.stream().map(PipelineInfo::name).toList();
    var removable = pipelineInfos.stream().noneMatch(PipelineInfo::running);
    var retentionConfigured = hasRetention(measure.getRetentionTime());
    var lastExport = getLastExport(measure.getRetentionTime());
    var lastRetentionStatus = getLastRetentionStatus(measure.getRetentionTime());

    return new DatasetSummaryDto(
        measure.getElementId(),
        measure.getMeasureName(),
        retentionConfigured,
        lastExport,
        lastRetentionStatus,
        pipelines,
        removable
    );
  }

  private Map<String, List<PipelineInfo>> getVisiblePipelinesByMeasure(Authentication auth) {
    return pipelineStorage.findAll().stream()
        .filter(pipeline -> permissionEvaluator.hasPermission(auth, pipeline.getPipelineId(), "READ"))
        .flatMap(pipeline -> extractSinks(pipeline).stream()
            .map(this::getMeasureName)
            .flatMap(Optional::stream)
            .map(measureName -> new PipelineMeasure(
                measureName,
                new PipelineInfo(pipeline.getName(), pipeline.isRunning())
            )))
        .collect(Collectors.groupingBy(PipelineMeasure::measureName,
            Collectors.mapping(PipelineMeasure::pipelineInfo, Collectors.toList())));
  }

  private boolean canReadMeasure(Authentication auth, DataLakeMeasure measure) {
    return measure != null
        && measure.getElementId() != null
        && permissionEvaluator.hasPermission(auth, measure.getElementId(), "READ");
  }

  private List<DataSinkInvocation> extractSinks(Pipeline pipeline) {
    return pipeline.getActions().stream()
        .filter(sink -> DATA_LAKE_APP_ID.equals(sink.getAppId()))
        .toList();
  }

  private Optional<String> getMeasureName(DataSinkInvocation sink) {
    return sink.getStaticProperties().stream()
        .filter(sp -> MEASURE_FIELD_INTERNAL_NAME.equals(sp.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .findFirst()
        .map(this::sanitizeMeasureName);
  }

  private String sanitizeMeasureName(String measureName) {
    return FORBIDDEN_MEASURE_NAME_CHARS.stream()
        .reduce(measureName, (currentName, forbiddenChar) -> currentName.replace(forbiddenChar, "_"));
  }

  private boolean hasRetention(RetentionTimeConfig retentionTime) {
    return retentionTime != null && retentionTime.getDataRetentionConfig() != null;
  }

  private Boolean getLastRetentionStatus(RetentionTimeConfig retentionTime) {
    if (retentionTime == null
        || retentionTime.getRetentionExportConfig() == null
        || retentionTime.getRetentionExportConfig().getRetentionLog() == null
        || retentionTime.getRetentionExportConfig().getRetentionLog().isEmpty()) {
      return null;
    }

    var retentionLog = retentionTime.getRetentionExportConfig().getRetentionLog();
    return retentionLog.get(retentionLog.size() - 1).getStatus();
  }

  private String getLastExport(RetentionTimeConfig retentionTime) {
    if (retentionTime == null || retentionTime.getRetentionExportConfig() == null) {
      return null;
    }

    return retentionTime.getRetentionExportConfig().getLastExport();
  }

  private record PipelineInfo(String name, boolean running) {
  }

  private record PipelineMeasure(String measureName, PipelineInfo pipelineInfo) {
  }
}

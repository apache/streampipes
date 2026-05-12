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

import org.apache.streampipes.model.connect.adapter.ChartSchemaUpdateInfo;
import org.apache.streampipes.model.datalake.DataExplorerWidgetHealthStatus;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChartSchemaUpdateCoordinator {

  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String DATA_LAKE_MEASUREMENT_FIELD = "db_measurement";
  private static final String SOURCE_CONFIGS = "sourceConfigs";
  private static final String MEASURE_NAME = "measureName";
  private static final String QUERY_CONFIG = "queryConfig";
  private static final String FIELDS = "fields";
  private static final String RUNTIME_NAME = "runtimeName";
  private static final String SELECTED = "selected";
  private static final String WIDGET_TITLE = "widgetTitle";

  private final IDataExplorerWidgetStorage widgetStorage;

  public ChartSchemaUpdateCoordinator() {
    this(StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerWidgetStorage());
  }

  ChartSchemaUpdateCoordinator(IDataExplorerWidgetStorage widgetStorage) {
    this.widgetStorage = widgetStorage;
  }

  public List<ChartSchemaUpdateInfo> checkChartMigrations(Pipeline pipeline,
                                                          EventSchema updatedSchema) {
    var measureNames = extractMeasureNames(pipeline);
    return widgetStorage
        .findAll()
        .stream()
        .map(widget -> makeUpdateInfo(widget, measureNames, updatedSchema))
        .flatMap(Optional::stream)
        .toList();
  }

  public void updateCharts(Pipeline pipeline,
                           EventSchema updatedSchema) {
    var measureNames = extractMeasureNames(pipeline);
    widgetStorage
        .findAll()
        .stream()
        .map(widget -> makeUpdateInfo(widget, measureNames, updatedSchema)
            .map(updateInfo -> Map.entry(widget, updateInfo)))
        .flatMap(Optional::stream)
        .forEach(entry -> {
          entry.getKey().setHealthStatus(DataExplorerWidgetHealthStatus.REQUIRES_ATTENTION);
          entry.getKey().setAffectedSchemaUpdateFields(entry.getValue().getAffectedFields());
          widgetStorage.updateElement(entry.getKey());
        });
  }

  Optional<ChartSchemaUpdateInfo> makeUpdateInfo(DataExplorerWidgetModel widget,
                                                 Set<String> measureNames,
                                                 EventSchema updatedSchema) {
    var matchingSourceConfigs = getSourceConfigs(widget)
        .stream()
        .filter(sourceConfig -> sourceConfig.get(MEASURE_NAME) instanceof String measureName
            && measureNames.contains(measureName))
        .toList();

    var affectedFields = matchingSourceConfigs
        .stream()
        .flatMap(sourceConfig -> findMissingSelectedFields(sourceConfig, updatedSchema).stream())
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (affectedFields.isEmpty()) {
      return Optional.empty();
    } else {
      var info = new ChartSchemaUpdateInfo();
      info.setChartId(widget.getElementId());
      info.setChartTitle(getChartTitle(widget));
      info.setMeasureName(matchingSourceConfigs.stream()
          .map(sourceConfig -> sourceConfig.get(MEASURE_NAME))
          .filter(String.class::isInstance)
          .map(String.class::cast)
          .findFirst()
          .orElse(null));
      info.setCanAutoMigrate(false);
      info.setAffectedFields(affectedFields.stream().toList());
      return Optional.of(info);
    }
  }

  private Set<String> findMissingSelectedFields(Map<String, Object> sourceConfig,
                                                EventSchema updatedSchema) {
    var updatedFieldNames = extractFieldNames(updatedSchema);
    return collectSelectedFields(sourceConfig)
        .stream()
        .filter(fieldName -> !updatedFieldNames.contains(fieldName))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<String> extractFieldNames(EventSchema schema) {
    if (schema == null || schema.getEventProperties() == null) {
      return Set.of();
    }

    return schema
        .getEventProperties()
        .stream()
        .map(EventProperty::getRuntimeName)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getSourceConfigs(DataExplorerWidgetModel widget) {
    var sourceConfigs = widget.getDataConfig().get(SOURCE_CONFIGS);
    if (sourceConfigs instanceof List<?> configs) {
      return configs
          .stream()
          .filter(Map.class::isInstance)
          .map(config -> (Map<String, Object>) config)
          .toList();
    } else {
      return List.of();
    }
  }

  private Set<String> collectSelectedFields(Map<String, Object> sourceConfig) {
    var selectedFields = new LinkedHashSet<String>();
    var queryConfig = sourceConfig.get(QUERY_CONFIG);
    if (queryConfig instanceof Map<?, ?> queryConfigMap) {
      collectFieldConfigs(queryConfigMap.get(FIELDS), selectedFields);
    }
    return selectedFields;
  }

  private void collectFieldConfigs(Object fieldConfigs,
                                   Set<String> selectedFields) {
    if (fieldConfigs instanceof List<?> fields) {
      fields
          .stream()
          .filter(Map.class::isInstance)
          .map(Map.class::cast)
          .filter(field -> Boolean.TRUE.equals(field.get(SELECTED)))
          .map(field -> field.get(RUNTIME_NAME))
          .filter(String.class::isInstance)
          .map(String.class::cast)
          .forEach(selectedFields::add);
    }
  }

  private Set<String> extractMeasureNames(Pipeline pipeline) {
    if (pipeline.getActions() == null) {
      return Set.of();
    }

    return pipeline
        .getActions()
        .stream()
        .filter(ChartSchemaUpdateCoordinator::isDataLakeSink)
        .map(this::extractMeasureName)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  private Optional<String> extractMeasureName(DataSinkInvocation sink) {
    return Optional
        .ofNullable(sink.getStaticProperties())
        .stream()
        .flatMap(List::stream)
        .filter(property -> DATA_LAKE_MEASUREMENT_FIELD.equals(property.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .filter(Objects::nonNull)
        .findFirst();
  }

  private String getChartTitle(DataExplorerWidgetModel widget) {
    var baseAppearanceConfig = widget.getBaseAppearanceConfig();
    if (baseAppearanceConfig != null && baseAppearanceConfig.get(WIDGET_TITLE) instanceof String widgetTitle) {
      return widgetTitle;
    } else if (widget.getWidgetId() != null) {
      return widget.getWidgetId();
    } else {
      return widget.getElementId();
    }
  }

  private static boolean isDataLakeSink(DataSinkInvocation dataSink) {
    return DATA_LAKE_SINK_APP_ID.equals(dataSink.getAppId());
  }
}

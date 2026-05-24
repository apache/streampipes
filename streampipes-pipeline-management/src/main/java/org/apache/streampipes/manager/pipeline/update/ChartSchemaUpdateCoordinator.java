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
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChartSchemaUpdateCoordinator {

  private static final String SOURCE_CONFIGS = "sourceConfigs";
  private static final String MEASURE_NAME = "measureName";
  private static final String MEASURE = "measure";
  private static final String QUERY_CONFIG = "queryConfig";
  private static final String FIELDS = "fields";
  private static final String RUNTIME_NAME = "runtimeName";
  private static final String SELECTED = "selected";
  private static final String WIDGET_TITLE = "widgetTitle";
  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
  };

  private final IDataExplorerWidgetStorage widgetStorage;
  private final ObjectMapper objectMapper;

  public ChartSchemaUpdateCoordinator() {
    this(StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerWidgetStorage());
  }

  ChartSchemaUpdateCoordinator(IDataExplorerWidgetStorage widgetStorage) {
    this.widgetStorage = widgetStorage;
    this.objectMapper = JacksonSerializer.getObjectMapper();
  }

  public List<ChartSchemaUpdateInfo> checkChartMigrations(Pipeline pipeline, EventSchema updatedSchema) {
    var measureNames = MeasurementUpdateUtils.extractMeasureNames(pipeline);
    return checkChartMigrations(measureNames, updatedSchema);
  }

  public List<ChartSchemaUpdateInfo> checkChartMigrations(Set<String> measureNames, EventSchema updatedSchema) {
    return widgetStorage
        .findAll()
        .stream()
        .map(widget -> makeUpdateInfo(widget, measureNames, updatedSchema))
        .flatMap(Optional::stream)
        .toList();
  }

  public void updateCharts(Pipeline pipeline,
                           EventSchema updatedSchema) {
    var measureNames = MeasurementUpdateUtils.extractMeasureNames(pipeline);
    updateCharts(measureNames, updatedSchema);
  }

  public void updateCharts(Set<String> measureNames, EventSchema updatedSchema) {
    widgetStorage
        .findAll()
        .forEach(widget -> updateChart(widget, measureNames, updatedSchema));
  }

  private void updateChart(DataExplorerWidgetModel widget,
                           Set<String> measureNames,
                           EventSchema updatedSchema) {
    var matchingSourceConfigs = getMatchingSourceConfigs(widget, measureNames);
    if (matchingSourceConfigs.isEmpty()) {
      return;
    }

    var updateInfo = makeUpdateInfo(widget, measureNames, updatedSchema);
    var measureSchemaUpdated = updateSourceConfigMeasures(matchingSourceConfigs, updatedSchema);
    updateInfo.ifPresent(info -> {
      widget.setHealthStatus(DataExplorerWidgetHealthStatus.REQUIRES_ATTENTION);
      widget.setAffectedSchemaUpdateFields(info.getAffectedFields());
    });

    if (measureSchemaUpdated || updateInfo.isPresent()) {
      widgetStorage.updateElement(widget);
    }
  }

  Optional<ChartSchemaUpdateInfo> makeUpdateInfo(DataExplorerWidgetModel widget,
                                                 Set<String> measureNames,
                                                 EventSchema updatedSchema) {
    var matchingSourceConfigs = getMatchingSourceConfigs(widget, measureNames);

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

  private List<Map<String, Object>> getMatchingSourceConfigs(DataExplorerWidgetModel widget,
                                                             Set<String> measureNames) {
    return getSourceConfigs(widget)
        .stream()
        .filter(sourceConfig -> sourceConfig.get(MEASURE_NAME) instanceof String measureName
            && measureNames.contains(measureName))
        .toList();
  }

  private boolean updateSourceConfigMeasures(List<Map<String, Object>> sourceConfigs,
                                             EventSchema updatedSchema) {
    return sourceConfigs
        .stream()
        .map(sourceConfig -> updateSourceConfigMeasure(sourceConfig, updatedSchema))
        .reduce(false, Boolean::logicalOr);
  }

  private boolean updateSourceConfigMeasure(Map<String, Object> sourceConfig,
                                            EventSchema updatedSchema) {
    if (sourceConfig.get(MEASURE_NAME) instanceof String measureName) {
      var measure = parseMeasure(sourceConfig.get(MEASURE), measureName);
      measure.setEventSchema(updatedSchema);
      sourceConfig.put(MEASURE, serializeMeasure(measure));
      return true;
    } else {
      return false;
    }
  }

  private DataLakeMeasure parseMeasure(Object measure,
                                       String measureName) {
    var dataLakeMeasure = objectMapper.convertValue(measure, DataLakeMeasure.class);
    if (dataLakeMeasure == null) {
      dataLakeMeasure = new DataLakeMeasure();
    }
    if (dataLakeMeasure.getMeasureName() == null) {
      dataLakeMeasure.setMeasureName(measureName);
    }
    if (dataLakeMeasure.getSchemaVersion() == null) {
      dataLakeMeasure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
    }
    return dataLakeMeasure;
  }

  private Map<String, Object> serializeMeasure(DataLakeMeasure measure) {
    return objectMapper.convertValue(measure, MAP_TYPE);
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

}

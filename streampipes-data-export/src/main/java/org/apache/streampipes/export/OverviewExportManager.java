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

package org.apache.streampipes.export;

import org.apache.streampipes.model.ResourceMetadata;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.model.export.AdapterOverviewEntry;
import org.apache.streampipes.model.export.ChartOverviewEntry;
import org.apache.streampipes.model.export.DashboardOverviewEntry;
import org.apache.streampipes.model.export.OverviewExport;
import org.apache.streampipes.model.export.PipelineOverviewEntry;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.resource.management.SpResourceManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class OverviewExportManager {

  private static final String SECURITY_MODE_KEY = "securityMode";
  private static final String SECURITY_POLICY_KEY = "securityPolicy";

  public static OverviewExport getOverviewExport(SpResourceManager resourceManager) {
    var overview = new OverviewExport();
    overview.setGeneratedAt(System.currentTimeMillis());
    overview.setAdapters(collectAdapters(resourceManager));
    overview.setPipelines(collectPipelines(resourceManager));
    overview.setDashboards(collectDashboards(resourceManager));
    overview.setCharts(collectCharts(resourceManager));
    return overview;
  }

  private static List<AdapterOverviewEntry> collectAdapters(SpResourceManager resourceManager) {
    return resourceManager.manageAdapters().getDb().findAll().stream()
        .map(adapter -> toAdapterEntry(resourceManager, adapter))
        .collect(Collectors.toList());
  }

  private static List<PipelineOverviewEntry> collectPipelines(SpResourceManager resourceManager) {
    return resourceManager.managePipelines().getDb().findAll().stream()
        .map(pipeline -> toPipelineEntry(resourceManager, pipeline))
        .collect(Collectors.toList());
  }

  private static List<DashboardOverviewEntry> collectDashboards(SpResourceManager resourceManager) {
    return resourceManager.manageDashboards().getDb().findAll().stream()
        .map(dashboard -> toDashboardEntry(resourceManager, dashboard))
        .collect(Collectors.toList());
  }

  private static List<ChartOverviewEntry> collectCharts(SpResourceManager resourceManager) {
    return resourceManager.manageCharts().getDb().findAll().stream()
        .map(chart -> toChartEntry(resourceManager, chart))
        .collect(Collectors.toList());
  }

  private static AdapterOverviewEntry toAdapterEntry(SpResourceManager resourceManager,
                                                      AdapterDescription adapter) {
    var entry = new AdapterOverviewEntry();
    entry.setId(adapter.getElementId());
    entry.setName(adapter.getName());
    entry.setOwner(findOwner(resourceManager, resolveAdapterPermissionId(adapter)));
    entry.setCreatedAt(adapter.getCreatedAt());
    entry.setAdapterType(adapter.getAppId());
    entry.setRunning(adapter.isRunning());
    entry.setSecurityMode(extractSelectedOption(adapter, SECURITY_MODE_KEY));
    entry.setSecurityPolicy(extractSelectedOption(adapter, SECURITY_POLICY_KEY));
    return entry;
  }

  private static PipelineOverviewEntry toPipelineEntry(SpResourceManager resourceManager, Pipeline pipeline) {
    var entry = new PipelineOverviewEntry();
    entry.setId(pipeline.getPipelineId());
    entry.setName(pipeline.getName());
    entry.setOwner(findOwner(resourceManager, pipeline.getPipelineId()));
    entry.setCreatedAt(pipeline.getCreatedAt());
    entry.setRunning(pipeline.isRunning());
    return entry;
  }

  private static DashboardOverviewEntry toDashboardEntry(SpResourceManager resourceManager,
                                                          DashboardModel dashboard) {
    var entry = new DashboardOverviewEntry();
    entry.setId(dashboard.getElementId());
    entry.setName(dashboard.getName());
    entry.setOwner(findOwner(resourceManager, dashboard.getElementId()));
    entry.setCreatedAt(getCreatedAt(dashboard.getMetadata()));
    entry.setWidgetCount(dashboard.getWidgets() == null ? 0 : dashboard.getWidgets().size());
    return entry;
  }

  private static ChartOverviewEntry toChartEntry(SpResourceManager resourceManager,
                                                  DataExplorerWidgetModel chart) {
    var entry = new ChartOverviewEntry();
    entry.setId(chart.getElementId());
    entry.setName(extractWidgetTitle(chart));
    entry.setOwner(findOwner(resourceManager, chart.getElementId()));
    entry.setCreatedAt(getCreatedAt(chart.getMetadata()));
    entry.setWidgetType(chart.getWidgetType());
    return entry;
  }

  private static String extractWidgetTitle(DataExplorerWidgetModel chart) {
    return Optional.ofNullable(chart.getBaseAppearanceConfig())
        .map(config -> config.get("widgetTitle"))
        .map(Object::toString)
        .orElse(chart.getElementId());
  }

  private static Long getCreatedAt(ResourceMetadata metadata) {
    return metadata == null ? null : metadata.getCreatedAtEpochMs();
  }

  private static String resolveAdapterPermissionId(AdapterDescription adapter) {
    return adapter.getCorrespondingDataStreamElementId() != null
        ? adapter.getCorrespondingDataStreamElementId()
        : adapter.getElementId();
  }

  private static String findOwner(SpResourceManager resourceManager, String resourceId) {
    var permissions = resourceManager.managePermissions().findForObjectId(resourceId);
    if (permissions.isEmpty()) {
      return null;
    }
    var ownerSid = permissions.get(0).getOwnerSid();
    var principal = resourceManager.manageUsers().getPrincipalById(ownerSid);
    return principal != null ? principal.getUsername() : ownerSid;
  }

  private static String extractSelectedOption(AdapterDescription adapter, String internalName) {
    if (adapter.getConfig() == null) {
      return null;
    }
    return adapter.getConfig().stream()
        .filter(staticProperty -> internalName.equals(staticProperty.getInternalName()))
        .filter(OneOfStaticProperty.class::isInstance)
        .map(OneOfStaticProperty.class::cast)
        .map(OneOfStaticProperty::getOptions)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(Option::isSelected)
        .findFirst()
        .map(OverviewExportManager::extractOptionValue)
        .orElse(null);
  }

  private static String extractOptionValue(Option option) {
    return option.getInternalName() != null ? option.getInternalName() : option.getName();
  }
}

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


package org.apache.streampipes.export.resolver;

import org.apache.streampipes.model.dashboard.DashboardItem;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.resource.management.DashboardResourceManager;
import org.apache.streampipes.resource.management.ResourceDeletionManager;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashboardResolver extends AbstractResolver<DashboardModel> {

  private final DashboardResourceManager resourceManager;

  public DashboardResolver(DashboardResourceManager resourceManager) {
    this.resourceManager = resourceManager;
  }

  @Override
  public DashboardModel findDocument(String resourceId) {
    return resourceManager.getDb().getElementById(resourceId);
  }

  @Override
  public DashboardModel modifyDocumentForExport(DashboardModel doc) {
    doc.setRev(null);
    return doc;
  }

  @Override
  protected boolean existsDoc(DashboardModel doc) {
    return Objects.nonNull(doc) && doc.getElementId() != null;
  }

  @Override
  public DashboardModel readDocument(String serializedDoc) throws JsonProcessingException {
    return this.defaultMapper.readValue(serializedDoc, DashboardModel.class);
  }

  @Override
  public ExportItem convert(DashboardModel document) {
    return new ExportItem(document.getElementId(), document.getName(), true);
  }

  @Override
  public void writeDocument(String document, AssetExportConfiguration config) throws JsonProcessingException {
    resourceManager.getDb().persist(deserializeDocument(document));
  }

  @Override
  public DashboardModel deserializeDocument(String document) throws JsonProcessingException {
    return this.spMapper.readValue(document, DashboardModel.class);
  }

  @Override
  public void deleteDocument(String document, ResourceDeletionManager resourceDeletionManager) throws JsonProcessingException {
    var dashboard = readDocument(document);
    var resourceId = dashboard.getElementId();
    resourceDeletionManager.delete(resourceManager.getDb(), resourceId);
  }

  /**
   * Returns the element ids of the charts placed on the given dashboard.
   *
   * <p>Charts are referenced by {@link DashboardItem#getDataViewElementId()}; {@link DashboardItem#getId()} is
   * the id of the dashboard tile itself. Dashboards created before 0.99 stored the chart id in {@code id} and
   * had no {@code dataViewElementId}. {@code UniqueDashboardIdMigration} rewrites them on core startup, but a
   * legacy export package imported into a running instance keeps that layout until the next restart, so
   * {@code id} is used as a fallback for items without {@code dataViewElementId}.
   */
  public List<String> getCharts(String resourceId) {
    var document = findDocument(resourceId);
    return document.getWidgets()
        .stream()
        .map(this::getChartId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private String getChartId(DashboardItem item) {
    return item.getDataViewElementId() != null ? item.getDataViewElementId() : item.getId();
  }
}

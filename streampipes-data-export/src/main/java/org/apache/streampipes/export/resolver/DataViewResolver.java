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

import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.dashboard.DashboardItem;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.export.ExportItem;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataViewResolver extends AbstractResolver<DashboardModel> {

  @Override
  public DashboardModel findDocument(String resourceId) {
    return getNoSqlStore().getDataExplorerDashboardStorage().getDashboard(resourceId);
  }

  @Override
  public DashboardModel modifyDocumentForExport(DashboardModel doc) {
    doc.setCouchDbRev(null);
    return doc;
  }

  @Override
  protected boolean existsDoc(DashboardModel doc) {
    return Objects.nonNull(doc) && doc.getCouchDbId() != null;
  }

  @Override
  public DashboardModel readDocument(String serializedDoc) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(serializedDoc, DashboardModel.class);
  }

  @Override
  public ExportItem convert(DashboardModel document) {
    return new ExportItem(document.getCouchDbId(), document.getName(), true);
  }

  @Override
  public void writeDocument(String document) throws JsonProcessingException {
    getNoSqlStore().getDataExplorerDashboardStorage().storeDashboard(deserializeDocument(document));
  }

  @Override
  protected DashboardModel deserializeDocument(String document) throws JsonProcessingException {
    return this.spMapper.readValue(document, DashboardModel.class);
  }

  public List<String> getWidgets(String resourceId) {
    var document = findDocument(resourceId);
    return document.getWidgets().stream().map(DashboardItem::getId).collect(Collectors.toList());
  }
}

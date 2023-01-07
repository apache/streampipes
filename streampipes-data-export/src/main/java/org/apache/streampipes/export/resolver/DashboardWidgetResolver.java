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
import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.model.export.ExportItem;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DashboardWidgetResolver extends AbstractResolver<DashboardWidgetModel> {

  @Override
  public DashboardWidgetModel findDocument(String resourceId) {
    return getNoSqlStore().getDashboardWidgetStorage().getDashboardWidget(resourceId);
  }

  @Override
  public DashboardWidgetModel modifyDocumentForExport(DashboardWidgetModel doc) {
    doc.setRev(null);
    return doc;
  }

  @Override
  public DashboardWidgetModel readDocument(String serializedDoc) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(serializedDoc, DashboardWidgetModel.class);
  }

  @Override
  public ExportItem convert(DashboardWidgetModel document) {
    return new ExportItem(document.getId(), document.getVisualizationName(), true);
  }

  @Override
  public void writeDocument(String document) throws JsonProcessingException {
    getNoSqlStore().getDashboardWidgetStorage().storeDashboardWidget(deserializeDocument(document));
  }

  @Override
  protected DashboardWidgetModel deserializeDocument(String document) throws JsonProcessingException {
    return this.spMapper.readValue(document, DashboardWidgetModel.class);
  }
}

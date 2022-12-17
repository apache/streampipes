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
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.export.ExportItem;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DataViewWidgetResolver extends AbstractResolver<DataExplorerWidgetModel> {

  @Override
  public DataExplorerWidgetModel findDocument(String resourceId) {
    return getNoSqlStore().getDataExplorerWidgetStorage().getDataExplorerWidget(resourceId);
  }

  @Override
  public DataExplorerWidgetModel modifyDocumentForExport(DataExplorerWidgetModel doc) {
    doc.setRev(null);
    return doc;
  }

  @Override
  public DataExplorerWidgetModel readDocument(String serializedDoc) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(serializedDoc, DataExplorerWidgetModel.class);
  }

  @Override
  public ExportItem convert(DataExplorerWidgetModel document) {
    return new ExportItem(document.getId(), document.getWidgetId(), true);
  }

  @Override
  public void writeDocument(String document) throws JsonProcessingException {
    getNoSqlStore().getDataExplorerWidgetStorage().storeDataExplorerWidget(deserializeDocument(document));
  }

  @Override
  protected DataExplorerWidgetModel deserializeDocument(String document) throws JsonProcessingException {
    return this.defaultMapper.readValue(document, DataExplorerWidgetModel.class);
  }

}

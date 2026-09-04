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

import org.apache.streampipes.model.datalake.DatasetMetadata;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MeasurementResolver extends AbstractResolver<DatasetMetadata> {

  private final IDatasetMetadataStorage datasetStorage;

  public MeasurementResolver(IDatasetMetadataStorage datasetStorage) {
    this.datasetStorage = datasetStorage;
  }

  @Override
  public DatasetMetadata findDocument(String resourceId) {
    return datasetStorage.getElementById(resourceId);
  }

  @Override
  public DatasetMetadata modifyDocumentForExport(DatasetMetadata doc) {
    doc.setRev(null);
    return doc;
  }

  @Override
  public DatasetMetadata readDocument(String serializedDoc) throws JsonProcessingException {
    return this.defaultMapper.readValue(serializedDoc, DatasetMetadata.class);
  }

  @Override
  public ExportItem convert(DatasetMetadata document) {
    return new ExportItem(document.getElementId(), document.getMeasureName(), true);
  }

  @Override
  public void writeDocument(String document, AssetExportConfiguration config) throws JsonProcessingException {
    datasetStorage.persist(deserializeDocument(document));
  }

  @Override
  public DatasetMetadata deserializeDocument(String document) throws JsonProcessingException {
    return this.spMapper.readValue(document, DatasetMetadata.class);
  }

  @Override
  public void deleteDocument(String document) throws JsonProcessingException {
    var measurement = readDocument(document);
    var resourceId = measurement.getElementId();
    datasetStorage.deleteElementById(resourceId);
  }
}

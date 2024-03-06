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
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.file.FileMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FileResolver extends AbstractResolver<FileMetadata> {

  @Override
  public FileMetadata findDocument(String resourceId) {
    return getNoSqlStore().getFileMetadataStorage().getMetadataById(resourceId);
  }

  @Override
  public FileMetadata modifyDocumentForExport(FileMetadata doc) {
    doc.setRev(null);
    return doc;
  }

  @Override
  public FileMetadata readDocument(String serializedDoc) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(serializedDoc, FileMetadata.class);
  }

  @Override
  public ExportItem convert(FileMetadata document) {
    return new ExportItem(document.getFileId(), document.getFilename(), true);
  }

  @Override
  public void writeDocument(String document) throws JsonProcessingException {
    getNoSqlStore().getFileMetadataStorage().addFileMetadata(deserializeDocument(document));
  }

  @Override
  protected FileMetadata deserializeDocument(String document) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(document, FileMetadata.class);
  }
}

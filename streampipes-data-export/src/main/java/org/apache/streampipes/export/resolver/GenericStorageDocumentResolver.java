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
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lightcouch.DocumentConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class GenericStorageDocumentResolver extends AbstractResolver<Map<String, Object>> {

  private static final Logger LOG = LoggerFactory.getLogger(GenericStorageDocumentResolver.class);

  @Override
  public Map<String, Object> findDocument(String resourceId) {
    try {
      return getNoSqlStore().getGenericStorage().findOne(resourceId);
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public Map<String, Object> modifyDocumentForExport(Map<String, Object> document) {
    document.remove("_rev");
    return document;
  }

  @Override
  public Map<String, Object> readDocument(String serializedDocument) throws JsonProcessingException {
    return SerializationUtils.getDefaultObjectMapper().readValue(serializedDocument, new TypeReference<>() {
    });
  }

  @Override
  public ExportItem convert(Map<String, Object> document) {
    var documentId = document.get("_id").toString();
    return new ExportItem(documentId, documentId, true);
  }

  @Override
  public void writeDocument(String document, AssetExportConfiguration config)
      throws JsonProcessingException, DocumentConflictException {
    try {
      getNoSqlStore().getGenericStorage().create(document);
    } catch (IOException e) {
      LOG.warn("Could not write document");
    }
  }

  @Override
  public Map<String, Object> deserializeDocument(String document) throws JsonProcessingException {
    return SerializationUtils.getDefaultObjectMapper().readValue(document, new TypeReference<>() {
    });
  }

  @Override
  public void deleteDocument(String document) throws JsonProcessingException {
    try {
      var asset = readDocument(document);
      var resourceId = asset.get("_id").toString();
      var storedAsset = getNoSqlStore().getGenericStorage().findOne(resourceId);
      getNoSqlStore().getGenericStorage().delete(resourceId, storedAsset.get("_rev").toString());
    } catch (IOException e) {
      // Do nothing
    }
  }

  @Override
  protected ObjectMapper getObjectMapper() {
    return defaultMapper;
  }


}

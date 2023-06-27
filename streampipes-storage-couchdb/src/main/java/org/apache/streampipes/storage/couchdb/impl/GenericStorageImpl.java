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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.file.GenericStorageAttachment;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericStorageImpl implements IGenericStorage {

  private static final String ID = "id";
  private static final String SLASH = "/";

  private final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
  };
  private final ObjectMapper mapper;

  public GenericStorageImpl() {
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public List<Map<String, Object>> findAll(String type) throws IOException {
    String query = getDatabaseRoute() + "/_design/appDocType/_view/appDocType?" + Utils
        .escapePathSegment("startkey=[\"" + type + "\"]&endkey=[\"" + type + "\",{}]&include_docs=true");
    Map<String, Object> queryResult = this.queryDocuments(query);

    List<Map<String, Object>> rows = (List<Map<String, Object>>) queryResult.get("rows");
    List<Map<String, Object>> result = new ArrayList<>();

    for (Map<String, Object> row : rows) {
      if (!((String) row.get(ID)).startsWith("_design")) {
        result.add((Map<String, Object>) row.get("doc"));
      }
    }

    return result;
  }

  @Override
  public Map<String, Object> findOne(String id) throws IOException {
    return this.queryDocuments(getDatabaseRoute() + SLASH + id);
  }

  @Override
  public Map<String, Object> create(String payload) throws IOException {
    Request req = Utils.postRequest(getDatabaseRoute(), payload);
    Content content = executeAndReturnContent(req);

    Map<String, Object> requestResult = deserialize(content.asString());
    return this.findOne((String) requestResult.get(ID));
  }

  @Override
  public <T> T create(T payload, Class<T> targetClass) throws IOException {
    Map<String, Object> result = this.create(this.mapper.writeValueAsString(payload));
    return this.mapper.convertValue(result, targetClass);
  }

  @Override
  public Map<String, Object> update(String id, String payload) throws IOException {
    Request req = Utils.putRequest(getDatabaseRoute() + SLASH + id, payload);
    Content content = executeAndReturnContent(req);

    Map<String, Object> requestResult = deserialize(content.asString());
    return this.findOne((String) requestResult.get(ID));
  }

  @Override
  public void delete(String id, String rev) throws IOException {
    Request req = Utils.deleteRequest(getDatabaseRoute() + SLASH + id + "?rev=" + rev);
    Content content = executeAndReturnContent(req);
  }

  @Override
  public void createAttachment(String docId,
                               String attachmentName,
                               String contentType,
                               byte[] payload,
                               String rev) throws IOException {
    Request req = Utils.putRequest(
            getDatabaseRoute() + SLASH + docId + SLASH + attachmentName + "?rev=" + rev, payload, contentType
    );
    executeAndReturnContent(req);
  }

  @Override
  public GenericStorageAttachment findAttachment(String docId, String attachmentName) throws IOException {
    Request req = Utils.getRequest(getDatabaseRoute() + SLASH + docId + SLASH + attachmentName);
    Content content =  executeAndReturnContent(req);
    return new GenericStorageAttachment(content.getType().getMimeType(), content.asBytes());
  }

  private Map<String, Object> queryDocuments(String route) throws IOException {
    Request req = Utils.getRequest(route);
    Content content = executeAndReturnContent(req);

    return deserialize(content.asString(StandardCharsets.UTF_8));
  }

  private Map<String, Object> deserialize(String payload) throws JsonProcessingException {

    return mapper.readValue(payload, typeRef);
  }

  private Content executeAndReturnContent(Request req) throws IOException {
    return req.execute().returnContent();
  }

  private String getDatabaseRoute() {
    return Utils.getDatabaseRoute(GenericCouchDbConstants.DB_NAME);
  }
}

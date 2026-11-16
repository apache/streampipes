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

package org.apache.streampipes.storage.couchdb.impl.system;

import org.apache.streampipes.couchdb.client.Attachments;
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.ViewQuery;
import org.apache.streampipes.couchdb.client.exception.BadRequestException;
import org.apache.streampipes.couchdb.client.exception.CouchDbAuthenticationException;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.exception.DatabaseNotFoundException;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.query.MangoQuery;
import org.apache.streampipes.model.file.GenericStorageAttachment;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.exceptions.StorageConflictException;
import org.apache.streampipes.storage.api.exceptions.StorageException;
import org.apache.streampipes.storage.api.exceptions.StorageNotFoundException;
import org.apache.streampipes.storage.api.exceptions.StorageQueryException;
import org.apache.streampipes.storage.api.exceptions.StorageUnavailableException;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generic documents of the {@code genericstorage} database, read and written as maps.
 */
public class GenericStorageImpl implements IGenericStorage {

  private static final String ID = "_id";
  private static final String REV = "_rev";
  private static final String APP_DOC_TYPE_VIEW = "appDocType";
  private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  private final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
  };

  private final ObjectMapper mapper;
  private final TypedCollection<Map<String, Object>> documents;
  private final Attachments attachments;

  public GenericStorageImpl(CouchDbStores stores) {
    this.mapper = JacksonSerializer.getObjectMapper();
    this.mapper
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    var database = stores.database(GenericCouchDbConstants.DB_NAME);
    this.documents = database.raw();
    this.attachments = database.attachments();
  }

  @Override
  public List<String> getAllAppDocTypes() {
    return query(() -> appDocTypeView().rows())
        .stream()
        .filter(row -> row.id() == null || !row.id().startsWith("_design"))
        .map(row -> appDocType(row.key()))
        .filter(Objects::nonNull)
        .filter(appDocType -> !appDocType.isBlank())
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  @Override
  public List<Map<String, Object>> findAll(String type) {
    return query(() -> appDocTypeView()
        .startKey(List.of(type))
        .endKey(List.of(type, Map.of()))
        .docs());
  }

  /**
   * This endpoint uses /db/_find endpoint of CouchDB to query documents.
   * You can find the docuemntation for the parameters at https://docs.couchdb.org/en/stable/api/database/find.html
   */
  @Override
  public List<Map<String, Object>> find(String appDocType,
                                        Map<String, Object> query) {
    return call(() -> documents.find(MangoQuery.fromMap(query)).docs());
  }

  @Override
  public List<Map<String, Object>> findAll(String appDocType,
                                           Map<String, Object> query) {
    return call(() -> documents.findAll(MangoQuery.fromMap(query)));
  }

  @Override
  public Map<String, Object> findOne(String id) {
    return call(() -> documents.get(id));
  }

  @Override
  public Map<String, Object> findOneWithNullIfEmpty(String id) {
    try {
      return findOne(id);
    } catch (StorageNotFoundException e) {
      return null;
    }
  }

  /**
   * Stores the document with {@code POST}, so that CouchDB assigns the id when the payload has none, as before.
   */
  @Override
  public Map<String, Object> create(String payload) {
    Map<String, Object> document = deserialize(payload);
    String id = call(() -> documents.post(document)).id();
    return this.findOne(id);
  }

  @Override
  public <T> T create(T payload, Class<T> targetClass) {
    Map<String, Object> result = this.create(toJson(payload));
    return this.mapper.convertValue(result, targetClass);
  }

  /**
   * Writes the document under the given id. Without {@code _rev} it is created, or rejected as a conflict if it
   * exists, which is what CouchDB does for the same {@code PUT}. With {@code _rev}, a document that does not exist is
   * never reported as not found: CouchDB usually rejects it as a conflict, but sometimes stores it.
   */
  @Override
  public Map<String, Object> update(String id, String payload) {
    Map<String, Object> document = deserialize(payload);
    document.put(ID, id);
    String storedId = call(() -> document.get(REV) != null
        ? documents.update(document)
        : documents.create(document)).id();
    return this.findOne(storedId);
  }

  @Override
  public void delete(String id, String rev) {
    if (rev == null) {
      throw new StorageQueryException("Deleting document " + id + " requires its revision");
    }
    call(() -> documents.delete(id, rev));
  }

  @Override
  public void createAttachment(String docId,
                               String attachmentName,
                               String contentType,
                               byte[] payload,
                               String rev) {
    call(() -> attachments.put(docId, rev, attachmentName,
        contentType != null ? contentType : DEFAULT_CONTENT_TYPE, payload));
  }

  @Override
  public GenericStorageAttachment findAttachment(String docId, String attachmentName) {
    var attachment = call(() -> attachments.get(docId, attachmentName))
        .orElseThrow(() -> new StorageNotFoundException("No attachment " + attachmentName + " in document " + docId));
    return new GenericStorageAttachment(mimeType(attachment.contentType()), attachment.data());
  }

  private ViewQuery<Map<String, Object>> appDocTypeView() {
    return documents.view(GenericCouchDbConstants.DESIGN_DOC_NAME, APP_DOC_TYPE_VIEW);
  }

  private Map<String, Object> deserialize(String payload) {
    try {
      return mapper.readValue(payload, typeRef);
    } catch (JsonProcessingException e) {
      throw new StorageQueryException("Document is not a valid JSON object", e);
    }
  }

  private String toJson(Object document) {
    try {
      return mapper.writeValueAsString(document);
    } catch (JsonProcessingException e) {
      throw new StorageException("Document could not be serialized", e);
    }
  }

  /**
   * First element of the {@code [appDocType, _id]} view key.
   */
  private static String appDocType(JsonNode key) {
    JsonNode type = key != null && key.isArray() && !key.isEmpty() ? key.get(0) : key;
    if (type == null || type.isNull() || type.isMissingNode()) {
      return null;
    }
    return type.isValueNode() ? type.asText() : type.toString();
  }

  /**
   * Media type without parameters such as {@code charset}.
   */
  private static String mimeType(String contentType) {
    if (contentType == null) {
      return null;
    }
    int separator = contentType.indexOf(';');
    return (separator >= 0 ? contentType.substring(0, separator) : contentType).trim();
  }

  private static <T> T call(CouchCall<T> call) {
    try {
      return call.run();
    } catch (CouchDbException | IllegalArgumentException e) {
      throw translate(e);
    }
  }

  /**
   * For reads of the view, where a 404 means that the design document is missing, not a document.
   */
  private static <T> T query(CouchCall<T> query) {
    try {
      return call(query);
    } catch (StorageNotFoundException e) {
      throw new StorageUnavailableException(e.getMessage(), e);
    }
  }

  private static StorageException translate(RuntimeException e) {
    return switch (e) {
      case DocumentNotFoundException _ -> new StorageNotFoundException(e.getMessage(), e);
      case DocumentConflictException _ -> new StorageConflictException(e.getMessage(), e);
      case BadRequestException _, IllegalArgumentException _ -> new StorageQueryException(e.getMessage(), e);
      case CouchDbTransportException _, CouchDbAuthenticationException _, DatabaseNotFoundException _ ->
          new StorageUnavailableException(e.getMessage(), e);
      case CouchDbException other when other.getStatus() >= 500 ->
          new StorageUnavailableException(e.getMessage(), e);
      default -> new StorageException(e.getMessage(), e);
    };
  }

  @FunctionalInterface
  private interface CouchCall<T> {
    T run();
  }

    @Override
  public void deleteAssetLinkToResource(String id) {
    
      var assets = this.findAll("asset-management");
      for (Map<String, Object>  asset :assets){

        deleteAssetLinks(asset, id);
        String docId = (String) asset.get("_id");
        String assetJson = toJson(asset);
        this.update(docId,assetJson);
      }
  }
  
  private void deleteAssetLinks(Map<String, Object>  asset, String resourceIdToDelete){

    if (asset.containsKey("assetLinks")) {
      List<Map<String, Object>> assetLinks = (List<Map<String, Object>>) asset.get("assetLinks");

      for (int i = 0; i < assetLinks.size(); i++) {
        Map<String, Object> link = assetLinks.get(i);
        if (link.containsKey("resourceId") && link.get("resourceId").equals(resourceIdToDelete)) {
          assetLinks.remove(i);
          i--; 
        }
      }
    }
    if (asset.containsKey("assets")) {
      List<Map<String, Object>> nestedAssets = (List<Map<String, Object>>) asset.get("assets");
      for (Map<String, Object> nestedAsset : nestedAssets) {
        deleteAssetLinks(nestedAsset, resourceIdToDelete);
            }
        }



  }
}

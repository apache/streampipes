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

package org.apache.streampipes.storage.api.system;

import org.apache.streampipes.model.file.GenericStorageAttachment;

import java.util.List;
import java.util.Map;

/**
 * Generic documents, read and written as maps.
 *
 * <p>Errors are reported with the unchecked {@link org.apache.streampipes.storage.api.exceptions.StorageException}:
 * {@code StorageNotFoundException} for a missing document or attachment, {@code StorageConflictException} for an
 * existing id or a revision that is no longer current, {@code StorageQueryException} for an invalid query or document
 * and {@code StorageUnavailableException} if the storage cannot serve the request.</p>
 */
public interface IGenericStorage {

  List<String> getAllAppDocTypes();

  List<Map<String, Object>> findAll(String type);

  List<Map<String, Object>> find(String appDocType, Map<String, Object> query);

  /**
   * Every document matching a query, where {@link #find(String, Map)} returns one page: at most 25 documents unless
   * the query sets a limit.
   *
   * @param appDocType document type
   * @param query      query in Mango syntax; a limit caps the total number of documents
   * @return all matches
   */
  List<Map<String, Object>> findAll(String appDocType, Map<String, Object> query);

  Map<String, Object> findOne(String id);

  /**
   * @param id document id
   * @return the document, or {@code null} if it does not exist; every other error is thrown
   */
  Map<String, Object> findOneWithNullIfEmpty(String id);

  Map<String, Object> create(String payload);

  <T> T create(T payload, Class<T> targetClass);

  Map<String, Object> update(String id, String payload);

  void delete(String id, String rev);

  void createAttachment(String docId, String attachmentName, String contentType, byte[] payload, String rev);

  GenericStorageAttachment findAttachment(String docId, String attachmentName);

  void deleteAssetLinkToResource(String id);
}

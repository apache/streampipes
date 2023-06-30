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

package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.file.GenericStorageAttachment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IGenericStorage {

  List<Map<String, Object>> findAll(String type) throws IOException;

  Map<String, Object> findOne(String id) throws IOException;

  Map<String, Object> create(String payload) throws IOException;

  <T> T create(T payload, Class<T> targetClass) throws IOException;

  Map<String, Object> update(String id, String payload) throws IOException;

  void delete(String id, String rev) throws IOException;

  void createAttachment(String docId, String attachmentName, String contentType, byte[] payload, String rev)
      throws IOException;

  GenericStorageAttachment findAttachment(String docId, String attachmentName) throws IOException;
}

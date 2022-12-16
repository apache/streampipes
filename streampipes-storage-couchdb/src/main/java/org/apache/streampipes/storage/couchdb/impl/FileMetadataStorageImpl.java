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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.IFileMetadataStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class FileMetadataStorageImpl extends AbstractDao<FileMetadata> implements
    IFileMetadataStorage {

  Logger logger = LoggerFactory.getLogger(NotificationStorageImpl.class);

  public FileMetadataStorageImpl() {
    super(Utils::getCouchDbFileMetadataClient, FileMetadata.class);
  }

  @Override
  public FileMetadata getMetadataById(String id) {
    return findWithNullIfEmpty(id);
  }

  @Override
  public List<FileMetadata> getAllFileMetadataDescriptions() {
    return findAll();
  }

  @Override
  public List<FileMetadata> getFilteredFileMetadataDescriptions(String filetype) {
    List<FileMetadata> allFiles = getAllFileMetadataDescriptions();
    return allFiles
        .stream()
        .filter(f -> f.getFiletype().equals(filetype))
        .collect(Collectors.toList());
  }

  @Override
  public void deleteFileMetadata(String id) {
    delete(id);
  }

  @Override
  public void addFileMetadata(FileMetadata fileMetadata) {
    persist(fileMetadata);
  }
}

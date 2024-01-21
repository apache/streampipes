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

package org.apache.streampipes.service.core.migrations.v095;

import org.apache.streampipes.manager.file.FileHandler;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IFileMetadataStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lightcouch.CouchDbClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeFilenamesAndRenameDuplicatesMigration implements Migration {

  private static final String ORIGINAL_FILENAME = "originalFilename";
  private static final String INTERNAL_FILENAME = "internalFilename";
  private static final String ID = "_id";

  private CouchDbClient couchDbClient = Utils.getCouchDbFileMetadataClient();
  private ObjectMapper mapper = new ObjectMapper();
  private IFileMetadataStorage fileMetadataStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getFileMetadataStorage();
  private FileHandler fileHandler = new FileHandler();
  // This map contains all fileMetadata grouped by originalFilename
  private Map<String, List<FileMetadata>> duplicateFilenamesFileMetadataMap = new HashMap<>();

  // Starting from v0.95, StreamPipes will use a single file name as the unique identifier of files instead of an
  // internal filename and an original filename. This migration merges them and renames all the files that have
  // duplicate names to ensure uniqueness
  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() {
    var couchDbRawFileMetadata = getCouchDbRawFileMetadata(getAllFileIds(fileMetadataStorage));
    couchDbRawFileMetadata.forEach(rawFileMetadata -> checkDuplicateOriginalFilename(rawFileMetadata));
    duplicateFilenamesFileMetadataMap.forEach(
        (originalFilename, fileMetadataList) -> update(originalFilename, fileMetadataList));
  }

  /**
   * Fetches all fileIds stored in CouchDB
   */
  private List<String> getAllFileIds(IFileMetadataStorage fileMetadataStorage) {
    return fileMetadataStorage.getAllFileMetadataDescriptions().stream().map(fileMetadata -> fileMetadata.getFileId())
        .toList();
  }

  /**
   * Takes the list of fileIds and searches for their raw metadata in CouchDB and returns them
   */
  private List<Map<String, Object>> getCouchDbRawFileMetadata(List<String> fileIds) {
    return fileIds.stream()
        .map(fileId -> convertInputStreamToMap(couchDbClient.find(fileId)))
        .toList();
  }

  /**
   * Converts InputStream (as stored in CouchDB) to Map, if there's an error, constructs a new Map
   */
  private Map<String, Object> convertInputStreamToMap(InputStream inputStream) {
    try {
      return mapper.readValue(inputStream, Map.class);
    } catch (Exception e) {
      return new HashMap<>();
    }
  }

  /**
   * Takes raw data stored in CouchDB and constructs duplicateFilenamesFileMetadataMap,
   * key is (possibly) duplicated originalFilename and value is that file's FileMetadata
   */
  private void checkDuplicateOriginalFilename(Map<String, Object> rawFileMetadata) {
    // If this file was already migrated or there was an error when converting InputStream to Map, skip it
    if (rawFileMetadata.containsKey(ORIGINAL_FILENAME)) {
      var originalFilename = rawFileMetadata.get(ORIGINAL_FILENAME).toString().toLowerCase();
      if (!duplicateFilenamesFileMetadataMap.containsKey(originalFilename)) {
        duplicateFilenamesFileMetadataMap.put(originalFilename, new ArrayList<>());
      }
      var fileMetadata = fileMetadataStorage.getMetadataById(rawFileMetadata.get(ID).toString());
      duplicateFilenamesFileMetadataMap.get(originalFilename).add(fileMetadata);
    }
  }

  /**
   * For each of the file, calls updateFileMetadata() and updateLocalFile()
   */
  private void update(String originalFilename, List<FileMetadata> fileMetadataList) {
    var fileMetadata = fileMetadataList.get(0);
    var internalFilename = getInternalFilenameFromFileMetadata(fileMetadata);
    // just name the 1st one to its originalFilename
    updateFileMetadata(fileMetadata, originalFilename);
    updateLocalFile(internalFilename, originalFilename);
    for (int i = 1; i < fileMetadataList.size(); ++i) {
      fileMetadata = fileMetadataList.get(i);
      internalFilename = getInternalFilenameFromFileMetadata(fileMetadataList.get(i));
      var newFilename = createNewFileName(i, removeFileType(originalFilename), fileMetadata.getFiletype());
      updateFileMetadata(fileMetadata, newFilename);
      updateLocalFile(internalFilename, newFilename);
    }
  }

  /**
   * Updates FileMetadata: sets new merged filename to the given filename
   */
  private void updateFileMetadata(FileMetadata fileMetadata, String filename) {
    fileMetadata.setFilename(filename);
    fileMetadataStorage.updateFileMetadata(fileMetadata);
  }

  /**
   * Updates the file stored locally: renames the file (i.e. before it's using internalFilename, now copy the
   * InputStream stored and replace internalFilename with the given filename)
   */
  private void updateLocalFile(String internalFilename, String filename) {
    fileHandler.renameFile(internalFilename, filename);
  }

  /**
   * Gets the old internalFilename after merging
   */
  private String getInternalFilenameFromFileMetadata (FileMetadata fileMetadata) {
    return convertInputStreamToMap(couchDbClient.find(fileMetadata.getFileId())).get(INTERNAL_FILENAME).toString();
  }

  /**
   * Creates the new file name for a file with a duplicate name.
   */
  private String createNewFileName(
      int index,
      String fileName,
      String fileType
  ) {
    return String.format(
        "%s(%d).%s",
        fileName,
        index + 1,
        fileType
    );
  }

  /**
   * Returns file name without file type suffix.
   */
  private String removeFileType(String fileName) {
    var indexBeforeFileType = fileName.lastIndexOf('.');
    return fileName.substring(0, indexBeforeFileType);
  }

  @Override
  public String getDescription() {
    return "Merge internalFilename and originalFilename. Additionally, rename"
        + "duplicate files to ensure uniqueness.";
  }
}

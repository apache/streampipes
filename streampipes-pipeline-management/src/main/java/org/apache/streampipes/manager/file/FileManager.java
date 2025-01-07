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
package org.apache.streampipes.manager.file;

import org.apache.streampipes.assetmodel.management.AssetModelHelper;
import org.apache.streampipes.commons.file.FileHasher;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {

  private static final Logger LOG = LoggerFactory.getLogger(FileManager.class);

  private final CRUDStorage<FileMetadata> fileMetadataStorage;
  private final FileHandler fileHandler;
  private final FileHasher fileHasher;

  public FileManager(CRUDStorage<FileMetadata> fileMetadataStorage,
                     FileHandler fileHandler,
                     FileHasher fileHasher) {
    this.fileMetadataStorage = fileMetadataStorage;
    this.fileHandler = fileHandler;
    this.fileHasher = fileHasher;
  }

  public FileManager() {
    this.fileMetadataStorage = StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getFileMetadataStorage();
    this.fileHandler = new FileHandler();
    this.fileHasher = new FileHasher();
  }

  public List<FileMetadata> getAllFiles() {
    return getAllFiles(null);
  }

  public List<FileMetadata> getAllFiles(String filetypes) {
    List<FileMetadata> allFiles = fileMetadataStorage.findAll();
    return filetypes != null ? filterFiletypes(allFiles, filetypes) : allFiles;
  }

  public File getFile(String filename) {
    return fileHandler.getFile(filename);
  }

  /**
   * Store a file in the internal file storage.
   * For csv files the bom is removed
   *
   * @param user who created the file
   * @param filename name of file
   * @param fileInputStream content of file
   * @return metadata of file
   */
  public FileMetadata storeFile(String user,
                                       String filename,
                                       InputStream fileInputStream) throws IOException {

    var filetype = filename.substring(filename.lastIndexOf(".") + 1);

    fileInputStream = validateFileNameAndCleanFile(filename, filetype, fileInputStream);

    var sanitizedFilename = sanitizeFilename(filename);

    writeToFile(sanitizedFilename, fileInputStream);

    return makeAndStoreFileMetadata(user, sanitizedFilename, filetype);
  }


  public void deleteFile(String elementId) {
    var fileMetadata = fileMetadataStorage.getElementById(elementId);
    if (fileMetadata != null) {
      fileHandler.deleteFile(fileMetadata.getFilename());
      fileMetadataStorage.deleteElementById(elementId);

      removeFileFromAllAssetLinks(elementId);
    }
  }

  private static void removeFileFromAllAssetLinks(String elementId) {
    var assetModelHelper = new AssetModelHelper();
    try {
      assetModelHelper.removeAssetLinkFromAllAssets(elementId);
    } catch (IOException e) {
      LOG.error("Could not remove asset link from file: {}", elementId, e);
    }
  }

  private InputStream validateFileNameAndCleanFile(String filename,
                                                  String filetype,
                                                  InputStream fileInputStream) {
    if (!validateFileType(filename)) {
      throw new IllegalArgumentException("Filetype for file %s not allowed".formatted(filename));
    }

    return cleanFile(fileInputStream, filetype);
  }

  /**
   * Remove Byte Order Mark (BOM) from csv files
   *
   * @param fileInputStream content of file
   * @param filetype file of type
   * @return input stream without BOM
   */
  protected InputStream cleanFile(InputStream fileInputStream, String filetype) {
    if (Filetypes.CSV.getFileExtensions().contains(filetype.toLowerCase())) {
      fileInputStream = new BOMInputStream(fileInputStream);
    }

    return fileInputStream;
  }

  public boolean checkFileContentChanged(String filename, String hash) throws IOException {
    var fileHash = fileHasher.hash(getFile(filename));
    return !fileHash.equals(hash);
  }

  public String sanitizeFilename(String filename) {
    return filename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
  }

  public boolean validateFileType(String filename) {
    return Filetypes.getAllFileExtensions()
                    .stream()
                    .anyMatch(filename::endsWith);
  }

  protected void writeToFile(String sanitizedFilename, InputStream fileInputStream) throws IOException {
    fileHandler.storeFile(sanitizedFilename, fileInputStream);
  }

  protected FileMetadata makeAndStoreFileMetadata(String user,
                                                       String sanitizedFilename,
                                                       String filetype) {
    var fileMetadata = makeFileMetadata(user, sanitizedFilename, filetype);
    storeFileMetadata(fileMetadata);

    return fileMetadata;
  }

  private FileMetadata makeFileMetadata(String user,
                                               String filename,
                                               String filetype) {

    FileMetadata fileMetadata = new FileMetadata();
    fileMetadata.setCreatedAt(System.currentTimeMillis());
    fileMetadata.setCreatedByUser(user);
    fileMetadata.setFiletype(filetype);
    fileMetadata.setFilename(filename);

    return fileMetadata;
  }

  private void storeFileMetadata(FileMetadata fileMetadata) {
    fileMetadataStorage.persist(fileMetadata);
  }


  private List<FileMetadata> filterFiletypes(List<FileMetadata> allFiles, String filetypes) {
    return allFiles
        .stream()
        .filter(fileMetadata -> Arrays
            .stream(filetypes.split(","))
            .anyMatch(ft -> ft.equals(fileMetadata.getFiletype())))
        .collect(Collectors.toList());
  }

}

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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.storage.api.IFileMetadataStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.io.input.BOMInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {

  public static List<FileMetadata> getAllFiles() {
    return getAllFiles(null);
  }

  public static List<FileMetadata> getAllFiles(String filetypes) {
    List<FileMetadata> allFiles = getFileMetadataStorage().getAllFileMetadataDescriptions();
    return filetypes != null ? filterFiletypes(allFiles, filetypes) : allFiles;
  }

  public static File getFileByOriginalName(String originalName) throws IOException {
    List<FileMetadata> allFiles = getFileMetadataStorage().getAllFileMetadataDescriptions();

    var file = allFiles
            .stream()
            .filter(fileMetadata -> fileMetadata.getFilename().equals(originalName))
            .findFirst();

    if (file.isEmpty()){
      throw new IOException("No file with original name '%s' found".formatted(originalName));
    }
    return new FileHandler().getFile(file.get().getFilename());
  }

  /**
   * Store a file in the internal file storage.
   * For csv files the bom is removed
   *
   * @param user            who created the file
   * @param filename
   * @param fileInputStream content of file
   * @return
   */
  public static FileMetadata storeFile(String user,
                                       String filename,
                                       InputStream fileInputStream) throws IOException {

    String filetype = filename.substring(filename.lastIndexOf(".") + 1);

    fileInputStream = cleanFile(fileInputStream, filetype);

    FileMetadata fileMetadata = makeFileMetadata(user, filename, filetype);
    new FileHandler().storeFile(filename, fileInputStream);
    storeFileMetadata(fileMetadata);
    return fileMetadata;
  }

  public static void deleteFile(String id) {
    FileMetadata fileMetadata = getFileMetadataStorage().getMetadataById(id);
    new FileHandler().deleteFile(fileMetadata.getFilename());
    getFileMetadataStorage().deleteFileMetadata(id);
  }

  public static File getFile(String filename) {
    return new FileHandler().getFile(filename);
  }

  /**
   * Remove Byte Order Mark (BOM) from csv files
   *
   * @param fileInputStream
   * @param filetype
   * @return
   */
  public static InputStream cleanFile(InputStream fileInputStream, String filetype) {
    if (Filetypes.CSV.getFileExtensions().contains(filetype.toLowerCase())) {
      fileInputStream = new BOMInputStream(fileInputStream);
    }

    return fileInputStream;
  }

  private static void storeFileMetadata(FileMetadata fileMetadata) {
    getFileMetadataStorage().addFileMetadata(fileMetadata);
  }

  private static IFileMetadataStorage getFileMetadataStorage() {
    return StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getFileMetadataStorage();
  }

  private static FileMetadata makeFileMetadata(String user,
                                               String filename,
                                               String filetype) {

    FileMetadata fileMetadata = new FileMetadata();
    fileMetadata.setCreatedAt(System.currentTimeMillis());
    fileMetadata.setCreatedByUser(user);
    fileMetadata.setFiletype(filetype);
    fileMetadata.setFilename(filename);

    return fileMetadata;
  }

  private static List<FileMetadata> filterFiletypes(List<FileMetadata> allFiles, String filetypes) {
    return allFiles
        .stream()
        .filter(fileMetadata -> Arrays
            .stream(filetypes.split(","))
            .anyMatch(ft -> ft.equals(fileMetadata.getFiletype())))
        .collect(Collectors.toList());
  }
}

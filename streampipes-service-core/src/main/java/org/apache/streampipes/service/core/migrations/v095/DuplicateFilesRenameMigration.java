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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DuplicateFilesRenameMigration implements Migration {
  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() {
    var fileMetadataStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                                        .getFileMetadataStorage();

    var filesToUpdate = getFilesToUpdate(fileMetadataStorage.getAllFileMetadataDescriptions());

    filesToUpdate.forEach(fileMetadata -> fileMetadataStorage.updateFileMetadata(fileMetadata));
  }


  /**
   * Takes the list of files and groups all files with the same name together.
   * The result is a list for each file name that has more than one file associated with it.
   */
  private List<List<FileMetadata>> getListsOfFilesWithSameName(List<FileMetadata> filesWithOldName) {
    var duplicateFileMap = filesWithOldName.stream()
                                           .collect(
                                               Collectors.groupingBy(file -> file.getOriginalFilename()
                                                                                 .toLowerCase()));
    return duplicateFileMap.values()
                           .stream()
                           .filter(files -> files.size() > 1)
                           .toList();
  }

  @Override
  public String getDescription() {
    return "Rename files with duplicate names.";
  }

  /**
   * Takes the files searches for duplicates and renames them and returns the files that must be updated
   */
  protected List<FileMetadata> getFilesToUpdate(List<FileMetadata> filesWithOldName) {

    var groupsOfFilesWithSameName = getListsOfFilesWithSameName(filesWithOldName);

    return groupsOfFilesWithSameName.stream()
                                    .flatMap(filesWithSameName ->
                                                 renameFilesWithSameName(filesWithSameName).stream())
                                    .collect(Collectors.toList());
  }


  /**
   * Takes a list of files with the same name renames them and returns the updated list
   */
  private List<FileMetadata> renameFilesWithSameName(List<FileMetadata> filesWithSameName) {
    return IntStream.range(1, filesWithSameName.size())
                    .mapToObj(i -> {
                      var metadata = filesWithSameName.get(i);
                      return renameFile(metadata, i);
                    })
                    .toList();
  }


  /**
   * Takes a file and renames it with a new name based the number of occurrences of the file name
   */
  private FileMetadata renameFile(FileMetadata fileMetadata, int index) {
    var oldFilename = fileMetadata.getOriginalFilename();

    var fileNameWithoutType = removeFileType(oldFilename);
    var fileTypeSuffix = getFileType(oldFilename);

    var newFileName = createNewFileName(index, fileNameWithoutType, fileTypeSuffix);

    fileMetadata.setOriginalFilename(newFileName);

    return fileMetadata;
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
        "%s(%d)%s",
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

  /**
   * Returns the file type a given file name.
   */
  private String getFileType(String fileName) {
    var indexBeforeFileType = fileName.lastIndexOf('.');
    return fileName.substring(indexBeforeFileType);
  }
}

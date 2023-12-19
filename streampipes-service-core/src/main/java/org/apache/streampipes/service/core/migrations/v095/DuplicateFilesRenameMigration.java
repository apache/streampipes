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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DuplicateFilesRenameMigration implements Migration {
  @Override
  public boolean shouldExecute() {
    return true;
  }

  // Starting from v0.95, StreamPipes will use file name as the unique identifier of files
  // This migration renames all the files that have duplicate names to ensure uniqueness
  @Override
  public void executeMigration() {
    var duplicateFiles = new HashMap<String, List<FileMetadata>>();
    var fileMetadataStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getFileMetadataStorage();
    for (var metadata : StorageDispatcher.INSTANCE.getNoSqlStore().getFileMetadataStorage()
        .getAllFileMetadataDescriptions()) {
      var originalFilename = metadata.getOriginalFilename().toLowerCase();
      if (!duplicateFiles.containsKey(originalFilename)) {
        duplicateFiles.put(originalFilename, new ArrayList<FileMetadata>());
      }
      duplicateFiles.get(originalFilename).add(metadata);
    }

    for (var metadataList : duplicateFiles.values()) {
      for (var i = 1; i < metadataList.size(); ++i) {
        var metadata = metadataList.get(i);
        var oldOriginalFilename = metadata.getOriginalFilename();
        var indexBeforeFileType = oldOriginalFilename.lastIndexOf('.');
        var newOriginalFilename =
            String.format("%s(%d)%s", oldOriginalFilename.substring(0, indexBeforeFileType), i + 1,
                oldOriginalFilename.substring(indexBeforeFileType));
        metadata.setOriginalFilename(newOriginalFilename);
        fileMetadataStorage.updateFileMetadata(metadata);
      }
    }

  }

  @Override
  public String getDescription() {
    return "Rename files with duplicate names.";
  }
}

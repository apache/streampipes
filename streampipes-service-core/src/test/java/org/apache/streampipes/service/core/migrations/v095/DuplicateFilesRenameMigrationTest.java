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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DuplicateFilesRenameMigrationTest {

  private DuplicateFilesRenameMigration migration;

  private static final String FILE_NAME = "file.txt";
  private static final String NEW_FILE_NAME_2 = "file(2).txt";
  private static final String NEW_FILE_NAME_3 = "file(3).txt";

  @Before
  public void setUp() {
    migration = new DuplicateFilesRenameMigration();
  }

  @Test
  public void getFilesToUpdateHandlesNoDuplicates() {
    var filesWithOldName = createFiles(List.of(
        FILE_NAME
    ));
    var result = migration.getFilesToUpdate(filesWithOldName);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getFilesToUpdateHandlesSingleDuplicate() {
    var filesWithOldName = createFiles(List.of(
        FILE_NAME,
        FILE_NAME
    ));
    var result = migration.getFilesToUpdate(filesWithOldName);
    assertEquals(1, result.size());
    assertEquals(
        NEW_FILE_NAME_2,
        result.get(0)
              .getOriginalFilename()
    );
  }

  @Test
  public void getFilesToUpdateHandlesMultipleDuplicates() {
    var filesWithOldName = createFiles(List.of(
        FILE_NAME,
        FILE_NAME,
        FILE_NAME
    ));
    var result = migration.getFilesToUpdate(filesWithOldName);
    assertEquals(2, result.size());
    assertEquals(
        NEW_FILE_NAME_2,
        result.get(0)
              .getOriginalFilename()
    );
    assertEquals(
        NEW_FILE_NAME_3,
        result.get(1)
              .getOriginalFilename()
    );
  }

  @Test
  public void getFilesToUpdateHandlesMixedDuplicatesAndUniqueFiles() {
    var filesWithOldName = createFiles(List.of(
        FILE_NAME,
        FILE_NAME,
        "unique.txt"
    ));
    var result = migration.getFilesToUpdate(filesWithOldName);
    assertEquals(1, result.size());
    assertEquals(
        NEW_FILE_NAME_2,
        result.get(0)
              .getOriginalFilename()
    );
  }

  /**
   * Creates a list of FileMetadata objects from a list of file names.
   */
  private List<FileMetadata> createFiles(List<String> fileNames) {
    return fileNames.stream()
                    .map(this::createFileMetadata)
                    .toList();
  }

  private FileMetadata createFileMetadata(String fileName) {
    var fileMetadata = new FileMetadata();
    fileMetadata.setOriginalFilename(fileName);
    return fileMetadata;
  }
}
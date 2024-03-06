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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.service.core.migrations.v095.MergeFilenamesAndRenameDuplicatesMigration.FILETYPE;
import static org.apache.streampipes.service.core.migrations.v095.MergeFilenamesAndRenameDuplicatesMigration.ID;
import static org.apache.streampipes.service.core.migrations.v095.MergeFilenamesAndRenameDuplicatesMigration.INTERNAL_FILENAME;
import static org.apache.streampipes.service.core.migrations.v095.MergeFilenamesAndRenameDuplicatesMigration.ORIGINAL_FILENAME;
import static org.junit.Assert.assertEquals;

public class MergeFilenamesAndRenameDuplicatesMigrationTest {

  private static final Map RAW_FILEMETADATA_1 = new HashMap<String, Object>() {
    {
      put(ID, "id1");
      put(ORIGINAL_FILENAME, "file.txt");
      put(INTERNAL_FILENAME, "doesn't matter");
      put(FILETYPE, "txt");
    }
  };

  private static final Map RAW_FILEMETADATA_2 = new HashMap<String, Object>() {
    {
      put(ID, "id2");
      put(ORIGINAL_FILENAME, "FILE.txt");
      put(INTERNAL_FILENAME, "doesn't matter");
      put(FILETYPE, "TXT");
    }
  };

  private static final Map RAW_FILEMETADATA_3 = new HashMap<String, Object>() {
    {
      put(ID, "id2");
      put(ORIGINAL_FILENAME, "fIlE.TxT");
      put(INTERNAL_FILENAME, "doesn't matter");
      put(FILETYPE, "TxT");
    }
  };

  private static final Map RAW_FILEMETADATA_4 = new HashMap<String, Object>() {
    {
      put(ID, "id3");
      put(ORIGINAL_FILENAME, "file.csv");
      put(INTERNAL_FILENAME, "doesn't matter");
      put(FILETYPE, "csv");
    }
  };

  private List<Map<String, Object>> couchDbRawFileMetadata;

  private MergeFilenamesAndRenameDuplicatesMigration migration;

  @Before
  public void setUp() {
    migration = new MergeFilenamesAndRenameDuplicatesMigration(true);
    couchDbRawFileMetadata = new ArrayList<>() {
      {
        add(RAW_FILEMETADATA_1);
        add(RAW_FILEMETADATA_2);
        add(RAW_FILEMETADATA_3);
        add(RAW_FILEMETADATA_4);
      }
    };
  }

  @Test
  public void testMigration() {
    // Test that the migration successfully groups FileMetadata by originalFilename
    migration.getFileMetadataToUpdate(couchDbRawFileMetadata);
    assertEquals(2, migration.fileMetadataGroupedByOriginalName.size());
    assertEquals(3, migration.fileMetadataGroupedByOriginalName.get("file.txt").size());
    assertEquals(1, migration.fileMetadataGroupedByOriginalName.get("file.csv").size());

    // Test that the migration successfully renames duplicate files
    migration.fileMetadataGroupedByOriginalName.forEach(
        (originalFilename, fileMetadataList) -> migration.update(originalFilename, fileMetadataList));
    assertEquals("file.txt", migration.fileMetadataGroupedByOriginalName.get("file.txt").get(0).getFilename());
    assertEquals("file(2).TXT", migration.fileMetadataGroupedByOriginalName.get("file.txt").get(1).getFilename());
    assertEquals("file(3).TxT", migration.fileMetadataGroupedByOriginalName.get("file.txt").get(2).getFilename());
    assertEquals("file.csv", migration.fileMetadataGroupedByOriginalName.get("file.csv").get(0).getFilename());
  }
}
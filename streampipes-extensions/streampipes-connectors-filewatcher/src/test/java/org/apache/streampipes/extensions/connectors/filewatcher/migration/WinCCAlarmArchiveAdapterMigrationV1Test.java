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

package org.apache.streampipes.extensions.connectors.filewatcher.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.filewatcher.adapter.WinCCAlarmArchiveAdapter;
import org.apache.streampipes.extensions.connectors.filewatcher.migration.config.WinCCAlarmArchiveAdapterVersionedConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WinCCAlarmArchiveAdapterMigrationV1Test {

  @Test
  void shouldInsertSegmentStartIndexWithDefaultValue() {
    var migration = new WinCCAlarmArchiveAdapterMigrationV1();
    var adapterDescription = WinCCAlarmArchiveAdapterVersionedConfig.getWinCCAlarmArchiveAdapterDescriptionV0();

    var migrated = migration.migrate(adapterDescription, (IStaticPropertyExtractor) null).element();

    assertEquals(8, migrated.getConfig().size());
    var startIndexProperty = (FreeTextStaticProperty) migrated.getConfig().get(4);
    assertEquals(WinCCAlarmArchiveAdapter.ARCHIVE_SEGMENT_START_INDEX, startIndexProperty.getInternalName());
    assertEquals("0", startIndexProperty.getValue());
  }
}

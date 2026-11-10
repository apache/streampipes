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

package org.apache.streampipes.audit.management;

import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.dataset.DataRetentionConfig;
import org.apache.streampipes.model.dataset.ExportConfig;
import org.apache.streampipes.model.dataset.RetentionAction;
import org.apache.streampipes.model.dataset.RetentionExportConfig;
import org.apache.streampipes.model.dataset.RetentionInterval;
import org.apache.streampipes.model.dataset.RetentionTimeConfig;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditRetentionManagerTest {

  @Test
  void deletesAuditEventsOlderThanTheConfiguredNumberOfDays() {
    var coreConfiguration = new SpCoreConfiguration();
    coreConfiguration.setAuditRetentionConfiguration(new RetentionTimeConfig(
        new DataRetentionConfig(RetentionInterval.DAILY, 7, RetentionAction.DELETE), null));
    var deletedBefore = new AtomicReference<Instant>();
    var store = new AuditEventStore() {
      @Override
      public void append(AuditEvent<?> event) {
      }

      @Override
      public void deleteEventsOlderThan(Instant cutoff) {
        deletedBefore.set(cutoff);
      }
    };

    new AuditRetentionManager(store, storage(coreConfiguration), null).cleanup();

    assertTrue(deletedBefore.get().isAfter(Instant.now().minus(8, ChronoUnit.DAYS)));
    assertTrue(deletedBefore.get().isBefore(Instant.now().minus(6, ChronoUnit.DAYS)));
  }

  @Test
  void startsTheNextExportAtThePreviousRetentionCutoff() {
    var lastExport = Instant.parse("2026-10-16T12:00:00Z");

    assertEquals(lastExport.minus(7, ChronoUnit.DAYS),
        AuditRetentionManager.exportStart(lastExport.toString(), 7));
  }

  @Test
  void defaultsStringExportFormatToCsv() {
    var exportConfig = new RetentionExportConfig(new ExportConfig("string", null, null, null), "provider", null, null);

    assertEquals(OutputFormat.CSV, AuditRetentionManager.outputFormat(exportConfig));
    assertEquals("csv", exportConfig.getExportConfig().format());
    assertEquals("comma", exportConfig.getExportConfig().csvDelimiter());
  }

  private ISpCoreConfigurationStorage storage(SpCoreConfiguration configuration) {
    return new ISpCoreConfigurationStorage() {
      @Override
      public boolean exists() {
        return true;
      }

      @Override
      public void createElement(SpCoreConfiguration element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public SpCoreConfiguration get() {
        return configuration;
      }

      @Override
      public SpCoreConfiguration updateElement(SpCoreConfiguration element) {
        return element;
      }

      @Override
      public void deleteElement() {
        throw new UnsupportedOperationException();
      }
    };
  }
}

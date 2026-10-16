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

import org.apache.streampipes.audit.api.AuditEntry;
import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriter;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriterFactory;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.export.objectstorage.ExportProviderFactory;
import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.dataset.ExportConfig;
import org.apache.streampipes.model.dataset.RetentionAction;
import org.apache.streampipes.model.dataset.RetentionExportConfig;
import org.apache.streampipes.model.dataset.RetentionLog;
import org.apache.streampipes.model.dataset.RetentionTimeConfig;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Exports audit events through configured providers before applying their retention action. */
public final class AuditRetentionManager {
  private static final String AUDIT_EXPORT_NAME = "audit-log";
  private static final int QUERY_PAGE_SIZE = 200;
  private static final int MAXIMUM_QUERY_RANGE_DAYS = 31;
  private static final List<String> COLUMNS = List.of(
      "recordedAt", "storedAt", "eventId", "eventType", "outcome", "actor", "resourceType", "resourceId", "details");

  private final AuditEventStore store;
  private final ISpCoreConfigurationStorage coreConfigurationStorage;
  private final ConfiguredOutputWriterFactory outputWriterFactory;

  public AuditRetentionManager(AuditEventStore store,
                               ISpCoreConfigurationStorage coreConfigurationStorage,
                               IFileMetadataStorage fileMetadataStorage) {
    this.store = store;
    this.coreConfigurationStorage = coreConfigurationStorage;
    this.outputWriterFactory = new ConfiguredOutputWriterFactory(fileMetadataStorage, coreConfigurationStorage);
  }

  public void cleanup() {
    var coreConfiguration = coreConfigurationStorage.get();
    if (coreConfiguration == null) {
      return;
    }
    var retention = coreConfiguration.getAuditRetentionConfiguration();
    if (retention == null || retention.getDataRetentionConfig() == null) {
      return;
    }
    var days = retention.getDataRetentionConfig().olderThanDays();
    if (days < 1) {
      throw new IllegalArgumentException("Audit retention must be at least one day");
    }

    var now = Instant.now();
    var cutoff = now.minus(days, ChronoUnit.MINUTES);
    var action = retention.getDataRetentionConfig().action();
    String exportPath = null;
    try {
      if (action != RetentionAction.DELETE) {
        exportPath = export(retention, cutoff);
      }
      if (action != RetentionAction.SAVE) {
        store.deleteEventsOlderThan(cutoff);
      }
      if (action != RetentionAction.DELETE) {
        updateExportStatus(coreConfiguration, retention.getRetentionExportConfig(), now, true, exportPath, "-");
      }
    } catch (Exception e) {
      if (action != RetentionAction.DELETE && retention.getRetentionExportConfig() != null) {
        updateExportStatus(coreConfiguration, retention.getRetentionExportConfig(), now, false, exportPath, e.getMessage());
      }
      throw e instanceof RuntimeException runtimeException
          ? runtimeException
          : new IllegalStateException("Audit retention export failed", e);
    }
  }

  private String export(RetentionTimeConfig retention, Instant cutoff) throws Exception {
    var exportConfig = requireExportConfig(retention);
    var exportProvider = findExportProvider(exportConfig.getExportProviderId());
    var format = outputFormat(exportConfig);
    var formatName = format.name().toLowerCase(Locale.ROOT);
    var params = new HashMap<String, String>();
    params.put("delimiter", exportConfig.getExportConfig().csvDelimiter());
    params.put("format", formatName);
    params.put("headerColumnName", exportConfig.getExportConfig().headerColumnName());
    params.put("missingValueBehaviour", exportConfig.getExportConfig().missingValueBehaviour());
    var writer = outputWriterFactory.getConfiguredWriter(null, format,
        new ProvidedRestQueryParams(AUDIT_EXPORT_NAME, params), false);
    var objectStorage = ExportProviderFactory.createExportProvider(exportProvider.getProviderType(), AUDIT_EXPORT_NAME,
        exportProvider, formatName);
    objectStorage.store(writeAuditEvents(writer, cutoff));
    return objectStorage.getFileName();
  }

  private StreamingResponseBody writeAuditEvents(ConfiguredOutputWriter writer,
                                                  Instant cutoff) {
    return output -> {
      writer.beforeFirstItem(output);
      boolean first = true;
      var reader = reader();
      for (var rangeStart = Instant.EPOCH; rangeStart.isBefore(cutoff);
           rangeStart = rangeEnd(rangeStart, cutoff)) {
        var rangeEnd = rangeEnd(rangeStart, cutoff);
        String cursor = null;
        do {
          var page = reader.query(new AuditQuery(rangeStart, rangeEnd, null, null, null, QUERY_PAGE_SIZE, cursor));
          for (var entry : page.items()) {
            writer.writeItem(output, row(reader, entry), COLUMNS, first);
            first = false;
          }
          cursor = page.nextCursor();
        } while (cursor != null);
      }
      writer.afterLastItem(output);
    };
  }

  private List<Object> row(AuditEventReader reader, AuditEntry entry) {
    var details = reader.find(UUID.fromString(entry.eventId()), entry.locator()).orElseThrow(
        () -> new IllegalStateException("Audit event disappeared during retention export"));
    var row = new ArrayList<Object>();
    row.add(details.recordedAt());
    row.add(entry.storedAt());
    row.add(entry.eventId());
    row.add(entry.eventType());
    row.add(entry.outcome());
    row.add(entry.actor());
    row.add(entry.resourceType());
    row.add(entry.resourceId());
    row.add(details.details());
    return row;
  }

  private AuditEventReader reader() {
    if (store instanceof AuditEventReader reader) {
      return reader;
    }
    throw new IllegalStateException("Audit export is unavailable");
  }

  private Instant rangeEnd(Instant start, Instant cutoff) {
    var candidate = start.plus(MAXIMUM_QUERY_RANGE_DAYS, ChronoUnit.DAYS);
    return candidate.isAfter(cutoff) ? cutoff : candidate;
  }

  private RetentionExportConfig requireExportConfig(RetentionTimeConfig retention) {
    var exportConfig = retention.getRetentionExportConfig();
    if (exportConfig == null || exportConfig.getExportConfig() == null
        || exportConfig.getExportProviderId() == null || exportConfig.getExportProviderId().isBlank()) {
      throw new IllegalArgumentException("Audit retention export configuration is required");
    }
    return exportConfig;
  }

  static OutputFormat outputFormat(RetentionExportConfig retentionExportConfig) {
    var exportConfig = retentionExportConfig.getExportConfig();
    if (exportConfig.format() == null || exportConfig.format().isBlank() || "string".equals(exportConfig.format())) {
      retentionExportConfig.setExportConfig(new ExportConfig(
          OutputFormat.CSV.name().toLowerCase(Locale.ROOT),
          exportConfig.csvDelimiter() == null ? "comma" : exportConfig.csvDelimiter(),
          exportConfig.missingValueBehaviour() == null ? "ignore" : exportConfig.missingValueBehaviour(),
          exportConfig.headerColumnName() == null ? "key" : exportConfig.headerColumnName()));
      return OutputFormat.CSV;
    }
    return OutputFormat.fromString(exportConfig.format());
  }

  private ExportProviderSettings findExportProvider(String providerId) {
    var coreConfiguration = coreConfigurationStorage.get();
    if (coreConfiguration == null || coreConfiguration.getExportProviderSettings() == null) {
      throw new IllegalArgumentException("Audit retention export provider was not found");
    }
    return coreConfiguration.getExportProviderSettings().stream()
        .filter(provider -> provider != null && providerId.equals(provider.getProviderId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Audit retention export provider was not found"));
  }

  private void updateExportStatus(org.apache.streampipes.model.configuration.SpCoreConfiguration coreConfiguration,
                                  RetentionExportConfig exportConfig,
                                  Instant now,
                                  boolean success,
                                  String path,
                                  String error) {
    if (success) {
      exportConfig.setLastExport(now.toString());
    }
    exportConfig.addRetentionLog(new RetentionLog(success, path, now.toString(), error));
    coreConfigurationStorage.updateElement(coreConfiguration);
  }
}

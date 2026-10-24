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

package org.apache.streampipes.audit.events.settings;

import org.apache.streampipes.audit.api.AuditDetails;
import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.model.configuration.EmailConfig;
import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.configuration.ProviderType;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportConfiguration;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsAuditRecorderTest {

  @Test
  void recordsBoundedAssetScopeForExportDownload() {
    var auditService = new CapturingAuditService();
    var recorder = new SettingsAuditRecorder(auditService);
    var assetConfigurations = new ArrayList<AssetExportConfiguration>();
    for (int i = 0; i < 101; i++) {
      var assetConfiguration = new AssetExportConfiguration();
      assetConfiguration.setAssetId("asset-" + i);
      assetConfigurations.add(assetConfiguration);
    }
    var exportConfiguration = new ExportConfiguration();
    exportConfiguration.setAssetExportConfiguration(assetConfigurations);

    recorder.exportDownloaded("user-1", AuditOutcome.REQUESTED, exportConfiguration);

    var call = auditService.calls.getFirst();
    var details = (AssetTransferDetails) call.details();
    assertEquals(StandardAuditEvents.EXPORT_DOWNLOAD, call.definition());
    assertEquals(AuditOutcome.REQUESTED, call.outcome());
    assertEquals(101, details.assetCount());
    assertEquals(100, details.assetIds().size());
  }

  @Test
  void recordsMailConfigurationChangesWithoutConfigurationValues() {
    var auditService = new CapturingAuditService();
    var recorder = new SettingsAuditRecorder(auditService);
    var before = new EmailConfig();
    before.setSmtpServerHost("old.example.org");
    before.setSmtpPassword("old-password");
    var after = new EmailConfig();
    after.setSmtpServerHost("new.example.org");
    after.setSmtpPassword("new-password");

    recorder.mailConfigurationUpdated("user-1", before, after);

    var call = auditService.calls.getFirst();
    var details = (MailAuditDetails) call.details();
    assertEquals(StandardAuditEvents.MAIL_CONFIGURATION_UPDATE, call.definition());
    assertEquals(List.of("smtpServerHost", "smtpPassword"), details.changedFields());
    assertFalse(details.toString().contains("old-password"));
    assertFalse(details.toString().contains("new-password"));
  }

  @Test
  void recordsProviderChangesWithoutProviderCredentials() {
    var auditService = new CapturingAuditService();
    var recorder = new SettingsAuditRecorder(auditService);
    var before = provider("old-access-key", "old-secret-key");
    var after = provider("new-access-key", "new-secret-key");

    recorder.exportProviderUpdated("user-1", before, after);

    var call = auditService.calls.getFirst();
    var details = (ExportProviderAuditDetails) call.details();
    assertEquals(StandardAuditEvents.EXPORT_PROVIDER_UPDATE, call.definition());
    assertEquals(List.of("accessKey", "secretKey"), details.changedFields());
    assertFalse(details.toString().contains("old-access-key"));
    assertFalse(details.toString().contains("new-secret-key"));
  }

  @Test
  void registersAllSettingsEventTypes() {
    var eventTypes = new StandardAuditEvents().eventTypes();

    assertTrue(eventTypes.containsAll(List.of(
        StandardAuditEvents.EXPORT_DOWNLOAD,
        StandardAuditEvents.IMPORT_EXECUTE,
        StandardAuditEvents.EXPORT_PROVIDER_CREATE,
        StandardAuditEvents.EXPORT_PROVIDER_UPDATE,
        StandardAuditEvents.EXPORT_PROVIDER_DELETE,
        StandardAuditEvents.FILE_UPLOAD,
        StandardAuditEvents.FILE_DELETE,
        StandardAuditEvents.MAIL_CONFIGURATION_UPDATE,
        StandardAuditEvents.MAIL_TEMPLATE_UPDATE)));
  }

  private ExportProviderSettings provider(String accessKey, String secretKey) {
    return new ExportProviderSettings(ProviderType.S3, "provider-1", accessKey, secretKey,
        "bucket", "https://storage.example.org", "eu-central-1");
  }

  private record AuditCall(AuditEventDefinition<?> definition, AuditOutcome outcome, AuditDetails details) {
  }

  private static final class CapturingAuditService implements AuditService {
    private final List<AuditCall> calls = new ArrayList<>();

    @Override
    public <T extends AuditDetails> void record(AuditEventDefinition<T> definition,
                                                AuditOutcome outcome,
                                                String actor,
                                                String resourceId,
                                                T details) {
      calls.add(new AuditCall(definition, outcome, details));
    }
  }
}

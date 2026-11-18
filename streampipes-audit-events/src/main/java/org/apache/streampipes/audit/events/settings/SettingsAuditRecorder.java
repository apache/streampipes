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

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;
import org.apache.streampipes.model.configuration.EmailConfig;
import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.model.file.FileMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Publishes bounded settings events without serializing configuration values or file contents. */
public final class SettingsAuditRecorder {
  private static final int MAX_ASSET_IDS = 100;

  private final AuditService auditService;

  public SettingsAuditRecorder(AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
  }

  public void exportDownloaded(String actor, AuditOutcome outcome, ExportConfiguration configuration) {
    auditService.record(StandardAuditEvents.EXPORT_DOWNLOAD, outcome, actor, null,
        transferDetails(configuration));
  }

  public void imported(String actor, AuditOutcome outcome, AssetExportConfiguration configuration) {
    auditService.record(StandardAuditEvents.IMPORT_EXECUTE, outcome, actor, null,
        transferDetails(configuration));
  }

  public void exportProviderCreated(String actor, ExportProviderSettings settings) {
    auditService.record(StandardAuditEvents.EXPORT_PROVIDER_CREATE, AuditOutcome.SUCCEEDED, actor,
        settings.getProviderId(), providerDetails(settings, List.of()));
  }

  public void exportProviderUpdated(String actor, ExportProviderSettings before, ExportProviderSettings after) {
    auditService.record(StandardAuditEvents.EXPORT_PROVIDER_UPDATE, AuditOutcome.SUCCEEDED, actor,
        after.getProviderId(), providerDetails(after, changedProviderFields(before, after)));
  }

  public void exportProviderDeleted(String actor, ExportProviderSettings settings) {
    auditService.record(StandardAuditEvents.EXPORT_PROVIDER_DELETE, AuditOutcome.SUCCEEDED, actor,
        settings.getProviderId(), providerDetails(settings, List.of()));
  }

  public void fileUploaded(String actor, FileMetadata file) {
    auditService.record(StandardAuditEvents.FILE_UPLOAD, AuditOutcome.SUCCEEDED, actor, file.getFileId(),
        new FileAuditDetails(file.getFiletype()));
  }

  public void fileUploadFailed(String actor) {
    auditService.record(StandardAuditEvents.FILE_UPLOAD, AuditOutcome.FAILED, actor, null, null);
  }

  public void fileDeleted(String actor, String fileId) {
    auditService.record(StandardAuditEvents.FILE_DELETE, AuditOutcome.SUCCEEDED, actor, fileId, null);
  }

  public void fileDeleteFailed(String actor, String fileId) {
    auditService.record(StandardAuditEvents.FILE_DELETE, AuditOutcome.FAILED, actor, fileId, null);
  }

  public void mailConfigurationUpdated(String actor, EmailConfig before, EmailConfig after) {
    auditService.record(StandardAuditEvents.MAIL_CONFIGURATION_UPDATE, AuditOutcome.SUCCEEDED, actor, null,
        new MailAuditDetails(changedMailFields(before, after)));
  }

  public void mailTemplateUpdated(String actor) {
    auditService.record(StandardAuditEvents.MAIL_TEMPLATE_UPDATE, AuditOutcome.SUCCEEDED, actor, null,
        new MailAuditDetails(List.of("template")));
  }

  private AssetTransferDetails transferDetails(List<String> assetIds,
                                                boolean overwriteExistingDocuments,
                                                boolean overrideBrokerSettings) {
    var allAssetIds = assetIds == null ? List.<String>of() : assetIds.stream()
        .filter(Objects::nonNull)
        .filter(id -> !id.isBlank())
        .toList();
    return new AssetTransferDetails(allAssetIds.stream().limit(MAX_ASSET_IDS).toList(), allAssetIds.size(),
        overwriteExistingDocuments, overrideBrokerSettings);
  }

  private AssetTransferDetails transferDetails(ExportConfiguration configuration) {
    if (configuration == null || configuration.getAssetExportConfiguration() == null) {
      return transferDetails(List.of(), false, false);
    }
    return transferDetails(configuration.getAssetExportConfiguration());
  }

  private AssetTransferDetails transferDetails(AssetExportConfiguration configuration) {
    return configuration == null ? transferDetails(List.of(), false, false)
        : transferDetails(List.of(configuration));
  }

  private AssetTransferDetails transferDetails(List<AssetExportConfiguration> configurations) {
    var validConfigurations = configurations == null ? List.<AssetExportConfiguration>of() : configurations.stream()
        .filter(Objects::nonNull)
        .toList();
    var assetIds = validConfigurations.stream().map(AssetExportConfiguration::getAssetId).toList();
    boolean overwriteExistingDocuments = validConfigurations.stream()
        .anyMatch(AssetExportConfiguration::isOverwriteExistingDocuments);
    boolean overrideBrokerSettings = validConfigurations.stream()
        .anyMatch(AssetExportConfiguration::isOverrideBrokerSettings);
    return transferDetails(assetIds, overwriteExistingDocuments, overrideBrokerSettings);
  }

  private ExportProviderAuditDetails providerDetails(ExportProviderSettings settings, List<String> changedFields) {
    var providerType = settings.getProviderType() == null ? null : settings.getProviderType().name();
    return new ExportProviderAuditDetails(providerType, changedFields);
  }

  private List<String> changedProviderFields(ExportProviderSettings before, ExportProviderSettings after) {
    var changes = new ArrayList<String>();
    addIfChanged(changes, "providerType", before.getProviderType(), after.getProviderType());
    addIfChanged(changes, "accessKey", before.getAccessKey(), after.getAccessKey());
    addIfChanged(changes, "secretKey", before.getSecretKey(), after.getSecretKey());
    addIfChanged(changes, "bucketName", before.getBucketName(), after.getBucketName());
    addIfChanged(changes, "endPoint", before.getEndPoint(), after.getEndPoint());
    addIfChanged(changes, "awsRegion", before.getAwsRegion(), after.getAwsRegion());
    return List.copyOf(changes);
  }

  private List<String> changedMailFields(EmailConfig before, EmailConfig after) {
    if (before == null || after == null) {
      return List.of("configuration");
    }
    var changes = new ArrayList<String>();
    addIfChanged(changes, "emailConfigured", before.isEmailConfigured(), after.isEmailConfigured());
    addIfChanged(changes, "transportStrategy", before.getTransportStrategy(), after.getTransportStrategy());
    addIfChanged(changes, "smtpServerHost", before.getSmtpServerHost(), after.getSmtpServerHost());
    addIfChanged(changes, "smtpServerPort", before.getSmtpServerPort(), after.getSmtpServerPort());
    addIfChanged(changes, "usesAuthentication", before.isUsesAuthentication(), after.isUsesAuthentication());
    addIfChanged(changes, "smtpUsername", before.getSmtpUsername(), after.getSmtpUsername());
    addIfChanged(changes, "smtpPassword", before.getSmtpPassword(), after.getSmtpPassword());
    addIfChanged(changes, "senderAddress", before.getSenderAddress(), after.getSenderAddress());
    addIfChanged(changes, "senderName", before.getSenderName(), after.getSenderName());
    addIfChanged(changes, "usesProxy", before.isUsesProxy(), after.isUsesProxy());
    addIfChanged(changes, "proxyHost", before.getProxyHost(), after.getProxyHost());
    addIfChanged(changes, "proxyPort", before.getProxyPort(), after.getProxyPort());
    addIfChanged(changes, "usesProxyAuthentication", before.isUsesProxyAuthentication(),
        after.isUsesProxyAuthentication());
    addIfChanged(changes, "proxyUser", before.getProxyUser(), after.getProxyUser());
    addIfChanged(changes, "proxyPassword", before.getProxyPassword(), after.getProxyPassword());
    addIfChanged(changes, "testRecipientAddress", before.getTestRecipientAddress(), after.getTestRecipientAddress());
    return List.copyOf(changes);
  }

  private void addIfChanged(List<String> changes, String field, Object before, Object after) {
    if (!Objects.equals(before, after)) {
      changes.add(field);
    }
  }
}

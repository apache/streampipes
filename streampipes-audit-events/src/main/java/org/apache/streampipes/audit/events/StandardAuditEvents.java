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

package org.apache.streampipes.audit.events;

import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditEventProvider;
import org.apache.streampipes.audit.events.adapter.AdapterCreatedDetails;
import org.apache.streampipes.audit.events.adapter.AdapterEditedDetails;
import org.apache.streampipes.audit.events.adapter.AdapterLifecycleDetails;
import org.apache.streampipes.audit.events.authentication.AuthenticationDetails;
import org.apache.streampipes.audit.events.settings.AssetTransferDetails;
import org.apache.streampipes.audit.events.settings.ExportProviderAuditDetails;
import org.apache.streampipes.audit.events.settings.FileAuditDetails;
import org.apache.streampipes.audit.events.settings.MailAuditDetails;

import java.util.List;

public final class StandardAuditEvents implements AuditEventProvider {

  public static final AuditEventDefinition<AdapterCreatedDetails> ADAPTER_CREATE =
      new AuditEventDefinition<>("sp.adapter.create", AdapterCreatedDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterLifecycleDetails> ADAPTER_START =
      new AuditEventDefinition<>("sp.adapter.start", AdapterLifecycleDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterLifecycleDetails> ADAPTER_STOP =
      new AuditEventDefinition<>("sp.adapter.stop", AdapterLifecycleDetails.class, "adapter");
  public static final AuditEventDefinition<AdapterEditedDetails> ADAPTER_EDIT =
      new AuditEventDefinition<>("sp.adapter.edit", AdapterEditedDetails.class, "adapter");

  public static final AuditEventDefinition<AuthenticationDetails> AUTH_LOGIN =
      new AuditEventDefinition<>("sp.auth.login", AuthenticationDetails.class);
  public static final AuditEventDefinition<AuthenticationDetails> AUTH_LOGOUT =
      new AuditEventDefinition<>("sp.auth.logout", AuthenticationDetails.class);

  public static final AuditEventDefinition<AssetTransferDetails> EXPORT_DOWNLOAD =
      new AuditEventDefinition<>("sp.export.download", AssetTransferDetails.class, "export");
  public static final AuditEventDefinition<AssetTransferDetails> IMPORT_EXECUTE =
      new AuditEventDefinition<>("sp.import.execute", AssetTransferDetails.class, "import");

  public static final AuditEventDefinition<ExportProviderAuditDetails> EXPORT_PROVIDER_CREATE =
      new AuditEventDefinition<>("sp.export-provider.create", ExportProviderAuditDetails.class, "export-provider");
  public static final AuditEventDefinition<ExportProviderAuditDetails> EXPORT_PROVIDER_UPDATE =
      new AuditEventDefinition<>("sp.export-provider.update", ExportProviderAuditDetails.class, "export-provider");
  public static final AuditEventDefinition<ExportProviderAuditDetails> EXPORT_PROVIDER_DELETE =
      new AuditEventDefinition<>("sp.export-provider.delete", ExportProviderAuditDetails.class, "export-provider");
  public static final AuditEventDefinition<FileAuditDetails> FILE_UPLOAD =
      new AuditEventDefinition<>("sp.file.upload", FileAuditDetails.class, "file");
  public static final AuditEventDefinition<FileAuditDetails> FILE_DELETE =
      new AuditEventDefinition<>("sp.file.delete", FileAuditDetails.class, "file");

  public static final AuditEventDefinition<MailAuditDetails> MAIL_CONFIGURATION_UPDATE =
      new AuditEventDefinition<>("sp.mail.configuration.update", MailAuditDetails.class, "mail-configuration");
  public static final AuditEventDefinition<MailAuditDetails> MAIL_TEMPLATE_UPDATE =
      new AuditEventDefinition<>("sp.mail.template.update", MailAuditDetails.class, "mail-template");

  private static final List<AuditEventDefinition<?>> EVENT_TYPES = List.of(
      ADAPTER_CREATE, ADAPTER_START, ADAPTER_STOP, ADAPTER_EDIT, AUTH_LOGIN, AUTH_LOGOUT,
      EXPORT_DOWNLOAD, IMPORT_EXECUTE,
      EXPORT_PROVIDER_CREATE, EXPORT_PROVIDER_UPDATE, EXPORT_PROVIDER_DELETE,
      FILE_UPLOAD, FILE_DELETE, MAIL_CONFIGURATION_UPDATE, MAIL_TEMPLATE_UPDATE);

  @Override
  public List<AuditEventDefinition<?>> eventTypes() {
    return EVENT_TYPES;
  }
}

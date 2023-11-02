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

package org.apache.streampipes.manager.migration;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.endpoint.HttpJsonParser;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

import static org.apache.streampipes.manager.migration.MigrationUtils.getRequestUrl;

public class AdapterDescriptionMigration093 extends AbstractMigrationManager {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterDescriptionMigration093.class);

  private final IAdapterStorage adapterDescriptionStorage;

  public AdapterDescriptionMigration093(IAdapterStorage adapterDescriptionStorage) {
    this.adapterDescriptionStorage = adapterDescriptionStorage;
  }

  public void reinstallAdapters(SpServiceRegistration extensionsServiceConfig) {
    var migrationProvider = AdapterDescriptionMigration093Provider.INSTANCE;
    if (migrationProvider.hasAppIdsToReinstall()) {
      var appIdsToReinstall = migrationProvider.getAppIdsToReinstall();
      var serviceUrl = extensionsServiceConfig.getServiceUrl();
      extensionsServiceConfig.getTags()
          .stream()
          .filter(tag -> tag.getPrefix() == SpServiceTagPrefix.ADAPTER)
          .filter(tag -> appIdsToReinstall.contains(tag.getValue()))
          .forEach(tag -> {
            var appId = tag.getValue();
            try {
              if (adapterDescriptionStorage.getAdaptersByAppId(appId).isEmpty()) {
                var requestUrl = getRequestUrl(SpServiceTagPrefix.ADAPTER, appId, serviceUrl);
                var entityPayload = HttpJsonParser.getContentFromUrl(URI.create(requestUrl));
                Operations.verifyAndAddElement(
                    entityPayload,
                    AuthTokenUtils.getAuthTokenForCurrentUser(),
                    true);
              }
            } catch (IOException | SepaParseException e) {
              LOG.warn("Could not reinstall adapter description {}", appId);
            }
          });
    }
  }
}

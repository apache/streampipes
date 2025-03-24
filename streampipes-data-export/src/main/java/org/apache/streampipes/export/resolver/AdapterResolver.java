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


package org.apache.streampipes.export.resolver;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.secret.SecretProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterResolver extends AbstractResolver<AdapterDescription> {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterResolver.class);

  @Override
  public AdapterDescription findDocument(String resourceId) {
    return getNoSqlStore().getAdapterInstanceStorage().getElementById(resourceId);
  }

  @Override
  public AdapterDescription modifyDocumentForExport(AdapterDescription adapterDescription) {
    adapterDescription.setRev(null);
    adapterDescription.setSelectedEndpointUrl(null);
    adapterDescription.setRunning(false);
    SecretProvider.getDecryptionService().apply(adapterDescription);

    return adapterDescription;
  }

  @Override
  public AdapterDescription readDocument(String serializedDoc) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().readValue(serializedDoc, AdapterDescription.class);
  }

  @Override
  public ExportItem convert(AdapterDescription document) {
    return new ExportItem(document.getElementId(), document.getName(), true);
  }

  @Override
  public void writeDocument(String document,
                            AssetExportConfiguration config) throws JsonProcessingException {
    var adapterDescription = deserializeDocument(document);
    if (config.isOverrideBrokerSettings()) {
      overrideProtocol(adapterDescription.getEventGrounding());
    }
    SecretProvider.getEncryptionService().apply(adapterDescription);
    getNoSqlStore().getAdapterInstanceStorage().persist(adapterDescription);
  }

  @Override
  public AdapterDescription deserializeDocument(String document) throws JsonProcessingException {
    return this.spMapper.readValue(document, AdapterDescription.class);
  }

  @Override
  public void deleteDocument(String document) throws JsonProcessingException {
    var adapter = deserializeDocument(document);
    var resourceId = adapter.getElementId();
    var existingAdapter = getNoSqlStore().getAdapterInstanceStorage().getElementById(resourceId);
    if (existingAdapter != null) {
      if (existingAdapter.isRunning()) {
        try {
          new AdapterMasterManagement(
              getNoSqlStore().getAdapterInstanceStorage(),
              new SpResourceManager().manageAdapters(),
              new SpResourceManager().manageDataStreams(),
              AdapterMetricsManager.INSTANCE.getAdapterMetrics()
          ).stopStreamAdapter(resourceId, true);
        } catch (AdapterException e) {
          LOG.warn("Error when stopping adapter with id {} and name {}", resourceId, existingAdapter.getName());
        }
      }
      getNoSqlStore().getAdapterInstanceStorage().deleteElementById(resourceId);
    }
  }

}

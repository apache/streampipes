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
package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ConnectWorkerDescriptionProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerDescriptionProvider.class);

  public List<AdapterDescription> getContainerDescription(String serviceGroup) {

    List<AdapterDescription> allAdapterDescriptions = new ArrayList<>();
    allAdapterDescriptions.addAll(getSpecificAdapterDescriptions(serviceGroup));
    allAdapterDescriptions.addAll(getGenericAdapterDescriptions(serviceGroup));

    return allAdapterDescriptions;
  }

  public Optional<AdapterDescription> getAdapterDescription(String appId) {
    List<AdapterDescription> allAdapterDescriptions = getContainerDescription("");
    return allAdapterDescriptions
        .stream()
        .filter(ad -> ad.getAppId().equals(appId))
        .findFirst();
  }

  private NamedStreamPipesEntity rewrite(NamedStreamPipesEntity entity) {
    // TODO remove after full internationalization support has been implemented
    if (entity.isIncludesLocales()) {
      LabelGenerator lg = new LabelGenerator(entity);
      try {
        entity = lg.generateLabels();
      } catch (IOException e) {
        LOG.error("Could not load labels for: " + entity.getAppId());
      }
    }
    return entity;
  }

  private List<AdapterDescription> getSpecificAdapterDescriptions(String serviceGroup) {
    List<AdapterDescription> result = new ArrayList<>();
    for (IAdapter<?> a : DeclarersSingleton.getInstance().getAllAdapters()) {
      AdapterDescription desc = (AdapterDescription) rewrite(a.declareModel());
      desc.setCorrespondingServiceGroup(serviceGroup);
      result.add(desc);
    }

    return result;
  }

  private List<AdapterDescription> getGenericAdapterDescriptions(String serviceGroup) {
    List<AdapterDescription> result = new ArrayList<>();

    Collection<IProtocol> allProtocols = DeclarersSingleton.getInstance().getAllProtocols();

    for (IProtocol p : allProtocols) {
      ProtocolDescription protocolDescription = (ProtocolDescription) rewrite(p.declareModel());

      if (protocolDescription.getSourceType().equals(AdapterSourceType.STREAM.toString())) {
        GenericAdapterStreamDescription desc = new GenericAdapterStreamDescription();
        desc.setName(protocolDescription.getName());
        desc.setDescription(protocolDescription.getDescription());
        desc.setIncludedAssets(protocolDescription.getIncludedAssets());
        desc.setElementId(protocolDescription.getElementId());
        desc.setAppId(protocolDescription.getAppId());
        desc.setProtocolDescription(protocolDescription);
        desc.setCorrespondingServiceGroup(serviceGroup);
        result.add(desc);
      } else if (protocolDescription.getSourceType().equals(AdapterSourceType.SET.toString())) {
        GenericAdapterSetDescription desc = new GenericAdapterSetDescription();
        desc.setName(protocolDescription.getName());
        desc.setDescription(protocolDescription.getDescription());
        desc.setIncludedAssets(protocolDescription.getIncludedAssets());
        desc.setElementId(protocolDescription.getElementId());
        desc.setAppId(protocolDescription.getAppId());
        desc.setProtocolDescription(protocolDescription);
        desc.setCorrespondingServiceGroup(serviceGroup);
        result.add(desc);
      }
    }

    return result;
  }
}

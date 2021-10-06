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
package org.apache.streampipes.connect.container.worker.init;

import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.IProtocol;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolSetDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolStreamDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectWorkerDescriptionProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerDescriptionProvider.class);

  public List<AdapterDescription> getContainerDescription(String serviceGroup) {

    List<AdapterDescription> adapters = new ArrayList<>();
    for (IAdapter<?> a : DeclarersSingleton.getInstance().getAllAdapters()) {
      AdapterDescription desc = (AdapterDescription) rewrite(a.declareModel());
      adapters.add(desc);
    }

    for (IProtocol p : DeclarersSingleton.getInstance().getAllProtocols()) {
      ProtocolDescription protocolDescription = (ProtocolDescription) rewrite(p.declareModel());

      if (protocolDescription instanceof ProtocolStreamDescription) {
        GenericAdapterStreamDescription desc = new GenericAdapterStreamDescription();
        desc.setAppId(protocolDescription.getAppId());
        desc.setProtocolDescription(protocolDescription);
        adapters.add(desc);
      } else if (protocolDescription instanceof ProtocolSetDescription) {
        GenericAdapterSetDescription desc = new GenericAdapterSetDescription();
        desc.setAppId(protocolDescription.getAppId());
        desc.setProtocolDescription(protocolDescription);
        adapters.add(desc);
      }
    }

    return adapters;
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
}

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

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ConnectWorkerDescriptionProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerDescriptionProvider.class);

  /**
   *  Retrieves a list of all adapter descriptions that are currently registered.
   *  @return a list of {@link AdapterDescription} objects representing the registered adapters
   */
  public List<AdapterDescription> getAdapterDescriptions() {
    return getRegisteredAdapters()
        .stream()
        .map(adapter -> applyLocales(adapter.declareConfig().getAdapterDescription()))
        .toList();
  }

  public Optional<AdapterDescription> getAdapterDescription(String id) {
    return getRegisteredAdapters()
        .stream()
        .map(ac -> ac.declareConfig().getAdapterDescription())
        .filter(ad -> ad.getAppId().equals(id))
        .findFirst();
  }

  /**
   * This is a helper method to mock the Declarer Singleton in unit tests
   * @return the registered adapters from the DeclarerSingleton
   */
  public Collection<StreamPipesAdapter> getRegisteredAdapters() {
    return DeclarersSingleton.getInstance().getAdapters();
  }


  private AdapterDescription applyLocales(AdapterDescription entity) {
    if (entity.isIncludesLocales()) {
      LabelGenerator lg = new LabelGenerator(entity);
      try {
        entity = (AdapterDescription) lg.generateLabels();
      } catch (IOException e) {
        LOG.error("Could not load labels for: " + entity.getAppId());
      }
    } else {
      LOG.error(
          "The adapter configuration of %s is missing the locales configurations. Add it to the declareConfig method"
              .formatted(entity.getAppId()));
    }
    return entity;
  }
}

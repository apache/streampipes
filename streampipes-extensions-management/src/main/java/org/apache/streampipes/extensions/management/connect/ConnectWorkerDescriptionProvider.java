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

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import java.util.Collection;
import java.util.Optional;

public class ConnectWorkerDescriptionProvider {

  public Optional<IAdapterConfiguration> getAdapterConfiguration(String id) {
    return getRegisteredAdapters().stream()
            .filter(ad -> ad.declareConfig().getAdapterDescription().getAppId().equals(id))
            .map(StreamPipesAdapter::declareConfig).findFirst();
  }

  public Optional<AdapterDescription> getAdapterDescription(String id) {
    return getRegisteredAdapters().stream().map(ac -> ac.declareConfig().getAdapterDescription())
            .filter(ad -> ad.getAppId().equals(id)).findFirst();
  }

  /**
   * This is a helper method to mock the Declarer Singleton in unit tests
   * 
   * @return the registered adapters from the DeclarerSingleton
   */
  public Collection<StreamPipesAdapter> getRegisteredAdapters() {
    return DeclarersSingleton.getInstance().getAdapters();
  }
}

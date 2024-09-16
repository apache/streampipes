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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.connect.management.compact.generator.AdapterModelGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import java.util.List;

public class CompactAdapterManagement {

  private final List<AdapterModelGenerator> generators;

  public CompactAdapterManagement(List<AdapterModelGenerator> generators) {
    this.generators = generators;
  }

  public AdapterDescription convertToAdapterDescription(CompactAdapter compactAdapter) throws Exception {

    var adapterDescription = findAdapterDescription(compactAdapter.appId());

    for (AdapterModelGenerator m : generators) {
      m.apply(adapterDescription, compactAdapter);
    }

    return adapterDescription;
  }

  public AdapterDescription convertToAdapterDescription(CompactAdapter compactAdapter,
                                                        AdapterDescription existingAdapter) throws Exception {
    var adapterDescription = convertToAdapterDescription(compactAdapter);

    existingAdapter.getDataStream().setEventSchema(adapterDescription.getDataStream().getEventSchema());
    existingAdapter.setRules(adapterDescription.getRules());
    existingAdapter.setConfig(adapterDescription.getConfig());
    existingAdapter.setDescription(adapterDescription.getDescription());
    existingAdapter.setName(adapterDescription.getName());

    return existingAdapter;
  }

  private AdapterDescription findAdapterDescription(String appId) {
    IAdapterStorage adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
    return adapterStorage.findAll()
        .stream()
        .filter(desc -> desc.getAppId()
            .equals(appId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}

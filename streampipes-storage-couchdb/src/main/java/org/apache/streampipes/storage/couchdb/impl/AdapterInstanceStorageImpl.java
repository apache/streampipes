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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class AdapterInstanceStorageImpl extends DefaultCrudStorage<AdapterDescription> implements IAdapterStorage {

   private static final Logger LOG = LoggerFactory.getLogger(AdapterInstanceStorageImpl.class.getCanonicalName());

  public AdapterInstanceStorageImpl() {
    super(Utils::getCouchDbAdapterInstanceClient, AdapterDescription.class);
  }

  @Override
  public AdapterDescription getFirstAdapterByAppId(String appId) {
    return this.findAll()
        .stream()
        .filter(p -> p.getAppId().equals(appId))
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public List<AdapterDescription> getAdaptersByAppId(String appId) {
    return this.findAll()
        .stream()
        .filter(p -> p.getAppId().equals(appId))
        .toList();
  }
  @Override
public List<AdapterDescription> findAll() {
  LOG.info("GOING INTO FIND ALL ");
    List<AdapterDescription> adapters = findAll("paginator/non_design_docs");
LOG.info(adapters.toString());
    // Filter out any malformed or incomplete AdapterDescriptions
    return adapters.stream()
                   .filter(adapter -> adapter.getDescription() != null)
                   .toList();
}
  
  public List<AdapterDescription> getAdapterPaginator(String startitem, int limit) {
    //TODO Fix this implementation
    List<AdapterDescription> pipelinesWithAdapter =
        couchDbClientSupplier
            .get()
            .view("paginator")
            .key(startitem)
            .query(AdapterDescription.class);
    return pipelinesWithAdapter;//.stream().map(p -> p.get("value").getAsString()).collect(Collectors.toList());
  }
}

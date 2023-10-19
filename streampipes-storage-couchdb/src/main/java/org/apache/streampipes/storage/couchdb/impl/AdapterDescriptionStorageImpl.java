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
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.dao.DbCommand;
import org.apache.streampipes.storage.couchdb.dao.FindCommand;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AdapterDescriptionStorageImpl extends AbstractDao<AdapterDescription> implements IAdapterStorage {

  public AdapterDescriptionStorageImpl() {
    super(Utils::getCouchDbAdapterDescriptionClient, AdapterDescription.class);
  }

  @Override
  public List<AdapterDescription> getAllAdapters() {
    return findAll();
  }

  @Override
  public String storeAdapter(AdapterDescription adapter) {
    return persist(adapter).v;
  }

  @Override
  public void updateAdapter(AdapterDescription adapter) {
    couchDbClientSupplier.get().update(adapter);
  }

  @Override
  public AdapterDescription getAdapter(String adapterId) {
    DbCommand<Optional<AdapterDescription>, AdapterDescription> cmd =
        new FindCommand<>(couchDbClientSupplier, adapterId, AdapterDescription.class);
    return cmd.execute().get();
  }

  @Override
  public void deleteAdapter(String adapterId) {

    AdapterDescription adapterDescription = getAdapter(adapterId);
    couchDbClientSupplier.get().remove(adapterDescription.getElementId(), adapterDescription.getRev());

  }

  @Override
  public AdapterDescription getFirstAdapterByAppId(String appId) {
    return getAll()
            .stream()
            .filter(p -> p.getAppId().equals(appId))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public List<AdapterDescription> getAdaptersByAppId(String appId) {
    return getAll()
            .stream()
            .filter(p -> p.getAppId().equals(appId))
            .toList();
  }

  @Override
  public List<AdapterDescription> getAll() {
    return findAll();
  }

  @Override
  public void createElement(AdapterDescription adapter) {
    persist(adapter);
  }

  @Override
  public AdapterDescription getElementById(String id) {
    return findWithNullIfEmpty(id);
  }

  @Override
  public AdapterDescription updateElement(AdapterDescription element) {
    var rev = getCurrentRev(element.getElementId());
    element.setRev(rev);
    update(element);
    return getElementById(element.getElementId());
  }

  @Override
  public void deleteElement(AdapterDescription element) {
    delete(element.getElementId());
  }

  private String getCurrentRev(String elementId) {
    return find(elementId).get().getRev();
  }
}

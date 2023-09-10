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

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class ExtensionsServiceStorageImpl extends AbstractDao<SpServiceRegistration>
    implements CRUDStorage<String, SpServiceRegistration> {

  public ExtensionsServiceStorageImpl() {
    super(Utils::getCouchDbExtensionsStorage, SpServiceRegistration.class);
  }


  @Override
  public List<SpServiceRegistration> getAll() {
    return findAll();
  }

  @Override
  public void createElement(SpServiceRegistration element) {
    persist(element);
  }

  @Override
  public SpServiceRegistration getElementById(String id) {
    return find(id).orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public SpServiceRegistration updateElement(SpServiceRegistration element) {
    update(element);
    return getElementById(element.getSvcId());
  }

  @Override
  public void deleteElement(SpServiceRegistration element) {
    delete(element.getSvcId());
  }
}

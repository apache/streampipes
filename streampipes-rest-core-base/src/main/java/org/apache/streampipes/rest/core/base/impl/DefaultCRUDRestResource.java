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

package org.apache.streampipes.rest.core.base.impl;

import org.apache.streampipes.model.shared.api.Storable;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class DefaultCRUDRestResource<T extends Storable>
    extends AbstractAuthGuardedRestResource
    implements CRUDResource<T, Void> {

  protected final CRUDStorage<T> storage;

  public DefaultCRUDRestResource(CRUDStorage<T> storage) {
    this.storage = storage;
  }

  @Override
  public List<T> findAll() {
    return storage.findAll();
  }

  @Override
  public T findById(@PathVariable String id) {
    return storage.getElementById(id);
  }

  @Override
  public void create(@RequestBody T entity) {
    storage.persist(entity);
  }

  @Override
  public Void update(@RequestBody T entity) {
    storage.updateElement(entity);
    return null;
  }

  @Override
  public void delete(@PathVariable String id) {
    storage.deleteElementById(id);
  }
}

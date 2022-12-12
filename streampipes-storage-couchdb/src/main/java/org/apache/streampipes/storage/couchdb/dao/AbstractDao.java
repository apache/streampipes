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
package org.apache.streampipes.storage.couchdb.dao;

import org.apache.streampipes.model.Tuple2;

import org.lightcouch.CouchDbClient;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class AbstractDao<T> extends CrudDao {

  private static final String ALL_DOCS = "_all_docs";

  protected Class<T> clazz;

  public AbstractDao(Supplier<CouchDbClient> couchDbClientSupplier, Class<T> clazz) {
    super(couchDbClientSupplier);
    this.clazz = clazz;
  }

  public Tuple2<Boolean, String> persist(T objToPersist) {
    return persist(objToPersist, clazz);
  }

  public Boolean delete(String key) {
    return delete(key, clazz);
  }

  public Boolean update(T objToUpdate) {
    return update(objToUpdate, clazz);
  }

  public Optional<T> find(String id) {
    return find(id, clazz);
  }

  public List<T> findAll() {
    return findAll(ALL_DOCS, clazz);
  }

  public List<T> findAll(String viewName) {
    return findAll(viewName, clazz);
  }

  public T findWithNullIfEmpty(String id) {
    return findWithNullIfEmpty(id, clazz);
  }

}

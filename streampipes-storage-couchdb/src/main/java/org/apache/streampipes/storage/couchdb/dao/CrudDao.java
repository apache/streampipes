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

public class CrudDao {

  protected Supplier<CouchDbClient> couchDbClientSupplier;

  public CrudDao(Supplier<CouchDbClient> couchDbClientSupplier) {
    this.couchDbClientSupplier = couchDbClientSupplier;
  }

  public <T> Tuple2<Boolean, String> persist(T objToPersist, Class<T> clazz) {
    DbCommand<Tuple2<Boolean, String>, T> cmd = new PersistCommand<>(couchDbClientSupplier,
        objToPersist,
        clazz);
    return cmd.execute();
  }

  public <T> Boolean delete(String key, Class<T> clazz) {
    DbCommand<Boolean, T> cmd = new DeleteCommand<>(couchDbClientSupplier, key, clazz);
    return cmd.execute();
  }

  public <T> Boolean update(T objToUpdate, Class<T> clazz) {
    DbCommand<Boolean, T> cmd = new UpdateCommand<>(couchDbClientSupplier, objToUpdate, clazz);
    return cmd.execute();
  }

  public <T> Optional<T> find(String id, Class<T> clazz) {
    DbCommand<Optional<T>, T> cmd = new FindCommand<>(couchDbClientSupplier, id, clazz);
    return cmd.execute();
  }

  public <T> List<T> findAll(String viewName,
                             Class<T> clazz) {
    DbCommand<List<T>, T> cmd = new FindAllCommand<>(couchDbClientSupplier, clazz, viewName);
    return cmd.execute();
  }

  public <T> T findWithNullIfEmpty(String id, Class<T> clazz) {
    DbCommand<Optional<T>, T> cmd = new FindCommand<>(couchDbClientSupplier, id, clazz);
    return cmd.execute().orElse(null);
  }
}

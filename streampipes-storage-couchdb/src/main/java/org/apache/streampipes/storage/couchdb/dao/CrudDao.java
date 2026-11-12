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

import org.apache.streampipes.couchdb.client.DocumentRef;
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.ViewQuery;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.storage.api.exceptions.StorageConflictException;
import org.apache.streampipes.storage.couchdb.CouchDbStore;

import java.util.List;
import java.util.Optional;

public class CrudDao {

  protected static final String ALL_DOCS = "_all_docs";

  protected final CouchDbStore store;

  public CrudDao(CouchDbStore store) {
    this.store = store;
  }

  public <T> Tuple2<Boolean, String> persist(T objToPersist, Class<T> clazz) {
    try {
      DocumentRef ref = collection(clazz).create(objToPersist);
      return new Tuple2<>(true, ref.id());
    } catch (DocumentConflictException e) {
      throw conflict(e);
    }
  }

  public <T> Boolean delete(String key, Class<T> clazz) {
    try {
      return collection(clazz).delete(key);
    } catch (DocumentConflictException e) {
      throw conflict(e);
    }
  }

  public <T> Boolean update(T objToUpdate, Class<T> clazz) {
    try {
      collection(clazz).update(objToUpdate);
      return true;
    } catch (DocumentNotFoundException e) {
      return false;
    } catch (DocumentConflictException e) {
      throw conflict(e);
    }
  }

  public <T> Optional<T> find(String id, Class<T> clazz) {
    return collection(clazz).find(id);
  }

  /**
   * All documents of a view, or of the database for {@code _all_docs} (without design documents).
   *
   * @param viewName {@code _all_docs} or {@code design/view}
   * @param clazz    document type
   * @param <T>      document type
   * @return the documents
   */
  public <T> List<T> findAll(String viewName, Class<T> clazz) {
    TypedCollection<T> collection = collection(clazz);
    return ALL_DOCS.equals(viewName) ? collection.findAll() : view(collection, viewName).docs();
  }

  public <T> T findWithNullIfEmpty(String id, Class<T> clazz) {
    return find(id, clazz).orElse(null);
  }

  protected <T> TypedCollection<T> collection(Class<T> clazz) {
    return store.typed(clazz);
  }

  protected <T> ViewQuery<T> view(String viewName, Class<T> clazz) {
    return view(collection(clazz), viewName);
  }

  protected static StorageConflictException conflict(DocumentConflictException e) {
    return new StorageConflictException(e.getMessage(), e);
  }

  private static <T> ViewQuery<T> view(TypedCollection<T> collection, String viewName) {
    int separator = viewName.indexOf('/');
    if (separator <= 0 || separator == viewName.length() - 1) {
      throw new IllegalArgumentException("View name must be <design document>/<view>: " + viewName);
    }
    return collection.view(viewName.substring(0, separator), viewName.substring(separator + 1));
  }
}

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

package org.apache.streampipes.storage.couchdb.impl.user;

import org.apache.streampipes.model.favorites.Favorite;
import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.storage.api.user.IFavoriteStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.CouchDbClient;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;

public class FavoriteStorageImpl implements IFavoriteStorage {

  private final Supplier<CouchDbClient> clientSupplier;

  public FavoriteStorageImpl() {
    this(() -> Utils.getCouchDbGsonClient("favorites"));
  }

  public FavoriteStorageImpl(Supplier<CouchDbClient> clientSupplier) {
    this.clientSupplier = clientSupplier;
  }

  @Override
  public List<Favorite> findAll() {
    var client = clientSupplier.get();
    try {
      return client.view("_all_docs").includeDocs(true).query(Favorite.class);
    } finally {
      client.shutdown();
    }
  }

  @Override
  public List<Favorite> findForUser(String userId) {
    var client = clientSupplier.get();
    try {
      String prefix = userPrefix(userId);
      return client.view("_all_docs")
          .startKey(prefix)
          .endKey(prefix + "\ufff0")
          .includeDocs(true)
          .query(Favorite.class);
    } finally {
      client.shutdown();
    }
  }

  @Override
  public boolean contains(String userId, FavoriteResourceType resourceType, String resourceId) {
    var client = clientSupplier.get();
    try {
      return client.contains(documentId(userId, resourceType, resourceId));
    } finally {
      client.shutdown();
    }
  }

  @Override
  public void add(Favorite favorite) {
    favorite.setElementId(documentId(favorite.getUserId(), favorite.getResourceType(), favorite.getResourceId()));
    var client = clientSupplier.get();
    try {
      client.save(favorite);
    } catch (DocumentConflictException e) {
      client.find(Favorite.class, favorite.getElementId());
    } finally {
      client.shutdown();
    }
  }

  @Override
  public void remove(String userId, FavoriteResourceType resourceType, String resourceId) {
    var client = clientSupplier.get();
    try {
      var favorite = client.find(Favorite.class, documentId(userId, resourceType, resourceId));
      client.remove(favorite);
    } catch (NoDocumentException e) {
     
    } catch (DocumentConflictException e) {
      if (client.contains(documentId(userId, resourceType, resourceId))) {
        throw e;
      }
    } finally {
      client.shutdown();
    }
  }

  static String documentId(String userId, FavoriteResourceType resourceType, String resourceId) {
    return userPrefix(userId) + resourceType.name() + ":" + encode(resourceId);
  }

  private static String userPrefix(String userId) {
    return encode(userId) + ":";
  }

  private static String encode(String value) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }
}

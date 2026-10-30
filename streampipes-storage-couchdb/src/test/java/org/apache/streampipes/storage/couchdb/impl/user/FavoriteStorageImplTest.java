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
import org.apache.streampipes.storage.couchdb.serializer.GsonSerializer;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FavoriteStorageImplTest {

  @Test
  void relationshipKeysAreStableAndSeparateUsersAndResourceTypes() {
    var keys = new HashSet<String>();
    for (var user : new String[]{"alice", "alice:", "bob", "üser"}) {
      for (var type : FavoriteResourceType.values()) {
        keys.add(FavoriteStorageImpl.documentId(user, type, "urn:resource/one"));
      }
    }
    assertEquals(20, keys.size());
    assertEquals(
        FavoriteStorageImpl.documentId("alice", FavoriteResourceType.ASSET, "urn:resource/one"),
        FavoriteStorageImpl.documentId("alice", FavoriteResourceType.ASSET, "urn:resource/one")
    );
    assertNotEquals(
        FavoriteStorageImpl.documentId("alice", FavoriteResourceType.ASSET, "urn:resource/one"),
        FavoriteStorageImpl.documentId("alice", FavoriteResourceType.ASSET, "urn:resource/two")
    );
  }

  @Test
  void arbitraryIdsCannotEscapeTheirUsersKeyRange() {
    String key = FavoriteStorageImpl.documentId("a:/b", FavoriteResourceType.CHART, "../x:CHART:ü");
    assertFalse(key.contains("/"));
    assertEquals(3, key.split(":").length);
    assertNotEquals(key, FavoriteStorageImpl.documentId("a", FavoriteResourceType.CHART, "/b:../x:CHART:ü"));
  }

  @Test
  void serializesOneRelationshipWithCouchDbIdentityAndNoResourceMetadata() {
    var gson = GsonSerializer.getGsonBuilder().create();
    var favorite = new Favorite("alice", FavoriteResourceType.DASHBOARD, "dashboard");
    favorite.setElementId(FavoriteStorageImpl.documentId("alice", FavoriteResourceType.DASHBOARD, "dashboard"));
    favorite.setRev("1-revision");
    var json = gson.toJsonTree(favorite).getAsJsonObject();
    assertEquals(favorite.getElementId(), json.get("_id").getAsString());
    assertEquals("1-revision", json.get("_rev").getAsString());
    assertEquals(5, json.size());
    var restored = gson.fromJson(json, Favorite.class);
    assertEquals("alice", restored.getUserId());
    assertEquals(FavoriteResourceType.DASHBOARD, restored.getResourceType());
    assertEquals("dashboard", restored.getResourceId());
  }
}


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
package org.apache.streampipes.user.management.service;

import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;

import java.util.List;

public class UserService {

  private JsonObject user;

  public UserService(String email) throws IllegalArgumentException {
    CouchDbClient dbClient = Utils.getCouchDbUserClient();
    List<JsonObject> users = dbClient.view("users/password").key(email).includeDocs(true).query(JsonObject.class);
    if (users.size() != 1) {
      throw new IllegalArgumentException("None or too many users with matching username");
    }
    this.user = users.get(0);

  }

  public String getPassword() {
    return user.get("password").getAsString();
  }
}

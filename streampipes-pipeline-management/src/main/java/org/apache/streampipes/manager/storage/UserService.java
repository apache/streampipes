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

package org.apache.streampipes.manager.storage;

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

  private IUserStorage userStorage;

  public UserService(IUserStorage userStorage) {
    this.userStorage = userStorage;
  }

  public List<Pipeline> getOwnPipelines(String email) {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getAllPipelines().stream().filter(p -> p
            .getCreatedByUser()
            .equals(email))
        .collect(Collectors.toList());
  }

  public void deleteOwnSource(String username, String sourceId) {
    if (checkUser(username)) {
      Principal user = getPrincipal(username);
      //user.getOwnSources().removeIf(a -> a.getElementId().equals(sourceId));
      userStorage.updateUser(user);
    }
  }

  /**
   * Get actions/sepas/sources
   */

  public List<String> getOwnActionUris(String username) {
    // TODO permissions
    return new ArrayList<>();
    //return userStorage.getUser(username)
    // .getOwnActions().stream().map(r -> r.getElementId()).collect(Collectors.toList());
  }

  public List<String> getOwnSepaUris(String username) {
    // TODO Permissions
    return new ArrayList<>();
    //return userStorage.getUser(username)
    // .getOwnSepas().stream().map(r -> r.getElementId()).collect(Collectors.toList());
  }


  public List<String> getOwnSourceUris(String email) {
    // TODO permissions
    return new ArrayList<>();
//    return userStorage
//            .getUser(email)
//            .getOwnSources()
//            .stream()
//            .map(r -> r.getElementId())
//            .collect(Collectors.toList());
  }

  private Principal getPrincipal(String username) {
    return userStorage.getUser(username);
  }


  /**
   * @param username
   * @return True if user exists exactly once, false otherwise
   */
  public boolean checkUser(String username) {
    return userStorage.checkUser(username);
  }

  private INoSqlStorage getStorageManager() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
  }

}

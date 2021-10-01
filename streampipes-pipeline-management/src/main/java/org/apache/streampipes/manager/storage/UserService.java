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

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.lightcouch.CouchDbClient;

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

  public Pipeline getPipeline(String username, String pipelineId) throws ElementNotFoundException {
    return StorageDispatcher
            .INSTANCE
            .getNoSqlStore()
            .getPipelineStorageAPI()
            .getAllPipelines()
            .stream()
            .filter(p -> p.getPipelineId().equals(pipelineId))
            .findFirst()
            .orElseThrow(ElementNotFoundException::new);
  }

  /**
   * Own Elements
   */

  public void addOwnPipeline(String username, Pipeline pipeline) {

    if (!checkUser(username)) {
      return;
    }
//        User user = userStorage.getUser(username);
//        user.addOwnPipeline(pipeline);
//        userStorage.updateUser(user);
    StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().storePipeline(pipeline);
  }

  public void addOwnSource(String username, String elementId, boolean publicElement) {
    if (!checkUser(username)) {
      return;
    }
    Principal user = getPrincipal(username);
    user.addOwnSource(elementId, publicElement);
    userStorage.updateUser(user);
  }

  public void addOwnAction(String username, String elementId, boolean publicElement) {
    if (!checkUser(username)) {
      return;
    }
    Principal user = getPrincipal(username);
    user.addOwnAction(elementId, publicElement);
    userStorage.updateUser(user);
  }

  public void addOwnSepa(String username, String elementId, boolean publicElement) {
    if (!checkUser(username)) {
      return;
    }
    Principal user = getPrincipal(username);
    user.addOwnSepa(elementId, publicElement);
    userStorage.updateUser(user);
  }

  public void deleteOwnAction(String username, String actionId) {
    if (checkUser(username)) {
      Principal user = getPrincipal(username);
      user.getOwnActions().removeIf(a -> a.getElementId().equals(actionId));
      userStorage.updateUser(user);
      //TODO remove actions from other users
    }
  }

  public void deleteOwnSource(String username, String sourceId) {
    if (checkUser(username)) {
      Principal user = getPrincipal(username);
      user.getOwnSources().removeIf(a -> a.getElementId().equals(sourceId));
      userStorage.updateUser(user);
    }
  }

  public void deleteOwnSepa(String username, String sepaId) {
    if (checkUser(username)) {
      Principal user = getPrincipal(username);
      user.getOwnSepas().removeIf(a -> a.getElementId().equals(sepaId));
      userStorage.updateUser(user);
    }
  }

  /**
   * Favorites
   */

  public void addSepaAsFavorite(String username, String elementId) {
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.addPreferredDataProcessor(elementId);
    userStorage.updateUser(user);
  }

  public void addActionAsFavorite(String username, String elementId) {
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.addPreferredDataSink(elementId);
    userStorage.updateUser(user);
  }

  public void addSourceAsFavorite(String username, String elementId) {
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.addPreferredDataStream(elementId);
    userStorage.updateUser(user);
  }

  public void removeSepaFromFavorites(String username, String elementId) {
    CouchDbClient dbClient = Utils.getCouchDbUserClient();
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.removePreferredDataProcessor(elementId);
    dbClient.update(user);
    dbClient.shutdown();
  }

  public void removeActionFromFavorites(String username, String elementId) {
    CouchDbClient dbClient = Utils.getCouchDbUserClient();
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.removePreferredDataSink(elementId);
    dbClient.update(user);
    dbClient.shutdown();
  }

  public void removeSourceFromFavorites(String username, String elementId) {
    CouchDbClient dbClient = Utils.getCouchDbUserClient();
    if (!checkUser(username)) {
      return;
    }
    UserAccount user = getUserAccount(username);
    user.removePreferredDataStream(elementId);
    dbClient.update(user);
    dbClient.shutdown();
  }

  /**
   * Get actions/sepas/sources
   */

  public List<String> getOwnActionUris(String username) {
    return userStorage.getUser(username).getOwnActions().stream().map(r -> r.getElementId()).collect(Collectors.toList());
  }

  public List<String> getFavoriteActionUris(String username) {
    return getUserAccount(username).getPreferredDataSinks();
  }

  public List<String> getAvailableActionUris(String principalName) {
    List<String> actions = new ArrayList<>(getOwnActionUris(principalName));
    userStorage
            .getAllUsers()
            .stream()
            .filter(u -> !(u.getPrincipalName().equals(principalName)))
            .map(u -> u.getOwnActions().stream().filter(p -> p.isPublicElement()).map(p -> p.getElementId()).collect(Collectors.toList())).forEach(actions::addAll);
    return actions;
  }

  public List<String> getOwnSepaUris(String username) {
    return userStorage.getUser(username).getOwnSepas().stream().map(r -> r.getElementId()).collect(Collectors.toList());
  }

  public List<String> getAvailableSepaUris(String principalName) {
    List<String> sepas = new ArrayList<>(getOwnSepaUris(principalName));
    userStorage
            .getAllUsers()
            .stream()
            .filter(u -> !(u.getPrincipalName().equals(principalName)))
            .map(u -> u.getOwnSepas().stream().filter(p -> p.isPublicElement()).map(p -> p.getElementId()).collect(Collectors.toList())).forEach(sepas::addAll);
    return sepas;
  }

  public List<String> getFavoriteSepaUris(String principalName) {
    return getUserAccount(principalName).getPreferredDataProcessors();
  }

  public List<String> getOwnSourceUris(String email) {
    return userStorage
            .getUser(email)
            .getOwnSources()
            .stream()
            .map(r -> r.getElementId())
            .collect(Collectors.toList());
  }

  public List<String> getAvailableSourceUris(String principalName) {
    List<String> sources = new ArrayList<>(getOwnSepaUris(principalName));
    userStorage
            .getAllUsers()
            .stream()
            .filter(u -> !(u.getPrincipalName().equals(principalName)))
            .map(u -> u.getOwnSources()
                    .stream()
                    .filter(p -> p.isPublicElement())
                    .map(p -> p.getElementId())
                    .collect(Collectors.toList()))
            .forEach(sources::addAll);
    return sources;
  }

  public List<String> getFavoriteSourceUris(String username) {
    return getUserAccount(username).getPreferredDataStreams();
  }

  public UserAccount getUserAccount(String principalName) {
    return (UserAccount) getPrincipal(principalName);
  }

  private Principal getPrincipal(String principalName) {
    return userStorage.getUser(principalName);
  }

  private IUserStorage userStorage() {
    return getStorageManager().getUserStorageAPI();
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

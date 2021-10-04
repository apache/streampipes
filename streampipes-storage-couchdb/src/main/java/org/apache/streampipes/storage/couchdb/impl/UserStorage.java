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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 */
public class UserStorage extends AbstractDao<Principal> implements IUserStorage {

  Logger LOG = LoggerFactory.getLogger(UserStorage.class);

  public UserStorage() {
    super(Utils::getCouchDbUserClient, Principal.class);
  }

  @Override
  public List<Principal> getAllUsers() {
    List<Principal> users = couchDbClientSupplier
            .get()
            .view("users/username")
            .includeDocs(true)
            .query(Principal.class);
    return new ArrayList<>(users);
  }

  @Override
  public List<UserAccount> getAllUserAccounts() {
    return getAllUsers()
            .stream()
            .filter(u -> u instanceof UserAccount)
            .map(u -> (UserAccount) u)
            .collect(Collectors.toList());
  }

  @Override
  public List<ServiceAccount> getAllServiceAccounts() {
    return getAllUsers()
            .stream()
            .filter(u -> u instanceof ServiceAccount)
            .map(u -> (ServiceAccount) u)
            .collect(Collectors.toList());
  }

  @Override
  public Principal getUser(String username) {
    // TODO improve
    CouchDbClient couchDbClient = couchDbClientSupplier.get();
    List<Principal> users = couchDbClient
            .view("users/username")
            .key(username)
            .includeDocs(true)
            .query(Principal.class);
    if (users.size() != 1) {
      LOG.error("None or to many users with matching username");
    }
    return users.size() > 0 ? users.get(0) : null;
  }

  @Override
  public UserAccount getUserAccount(String username) {
    return (UserAccount) getUser(username);
  }

  @Override
  public ServiceAccount getServiceAccount(String username) {
    return (ServiceAccount) getUser(username);
  }

  @Override
  public void storeUser(Principal user) {
    persist(user);
  }

  @Override
  public void updateUser(Principal user) {
    update(user);
  }

  @Override
  public boolean emailExists(String email) {
    List<UserAccount> users = getAllUserAccounts();
    return users
            .stream()
            .filter(u -> u.getEmail() != null)
            .anyMatch(u -> u.getEmail().equals(email));
  }

  /**
   * @param username
   * @return True if user exists exactly once, false otherwise
   */
  @Override
  public boolean checkUser(String username) {
    List<Principal> users = couchDbClientSupplier
            .get()
            .view("users/username")
            .key(username)
            .includeDocs(true)
            .query(Principal.class);

    return users.size() == 1;
  }

  @Override
  public void deleteUser(String principalId) {
    delete(principalId);
  }

  @Override
  public Principal getUserById(String principalId) {
    return findWithNullIfEmpty(principalId);
  }

}

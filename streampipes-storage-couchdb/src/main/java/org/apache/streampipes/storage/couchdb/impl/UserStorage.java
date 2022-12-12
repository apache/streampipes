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
import org.apache.streampipes.storage.couchdb.dao.CrudViewDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 */
public class UserStorage extends CrudViewDao implements IUserStorage {

  private static final Logger LOG = LoggerFactory.getLogger(UserStorage.class);
  private static final String viewName = "users/username";

  public UserStorage() {
    super(Utils::getCouchDbUserClient);
  }

  @Override
  public List<Principal> getAllUsers() {
    return findAll(viewName, Principal.class);
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
    List<Principal> users = findByKey(viewName, username.toLowerCase(), Principal.class);
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
    persist(user, Principal.class);
  }

  @Override
  public void updateUser(Principal user) {
    update(user, Principal.class);
  }

  /**
   * @param username
   * @return True if user exists exactly once, false otherwise
   */
  @Override
  public boolean checkUser(String username) {
    List<Principal> users = findByKey(viewName, username.toLowerCase(), Principal.class);

    return users.size() == 1;
  }

  @Override
  public void deleteUser(String principalId) {
    delete(principalId, Principal.class);
  }

  @Override
  public Principal getUserById(String principalId) {
    return findWithNullIfEmpty(principalId, Principal.class);
  }

}

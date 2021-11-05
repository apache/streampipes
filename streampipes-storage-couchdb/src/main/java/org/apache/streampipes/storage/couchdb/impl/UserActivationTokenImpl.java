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

import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.storage.api.IUserActivationTokenStorage;
import org.apache.streampipes.storage.couchdb.dao.CrudDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class UserActivationTokenImpl extends CrudDao implements IUserActivationTokenStorage {

  private static final String viewName = "users/user-activation";

  public UserActivationTokenImpl() {
    super(Utils::getCouchDbUserClient);
  }

  @Override
  public List<UserActivationToken> getAll() {
    return findAll(viewName, UserActivationToken.class);
  }

  @Override
  public void createElement(UserActivationToken element) {
    persist(element, UserActivationToken.class);
  }

  @Override
  public UserActivationToken getElementById(String s) {
    return findWithNullIfEmpty(s, UserActivationToken.class);
  }

  @Override
  public UserActivationToken updateElement(UserActivationToken element) {
    update(element, UserActivationToken.class);
    return getElementById(element.getToken());
  }

  @Override
  public void deleteElement(UserActivationToken element) {
    delete(element.getToken(), UserActivationToken.class);
  }
}

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

import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.storage.api.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.couchdb.dao.CrudDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class PasswordRecoveryTokenImpl extends CrudDao implements IPasswordRecoveryTokenStorage {

  private static final String viewName = "users/password-recovery";

  public PasswordRecoveryTokenImpl() {
    super(Utils::getCouchDbUserClient);
  }


  @Override
  public List<PasswordRecoveryToken> getAll() {
    return findAll(viewName, PasswordRecoveryToken.class);
  }

  @Override
  public void createElement(PasswordRecoveryToken element) {
    persist(element, PasswordRecoveryToken.class);
  }

  @Override
  public PasswordRecoveryToken getElementById(String s) {
    return findWithNullIfEmpty(s, PasswordRecoveryToken.class);
  }

  @Override
  public PasswordRecoveryToken updateElement(PasswordRecoveryToken element) {
    update(element, PasswordRecoveryToken.class);
    return getElementById(element.getToken());
  }

  @Override
  public void deleteElement(PasswordRecoveryToken element) {
    delete(element.getToken(), PasswordRecoveryToken.class);
  }
}

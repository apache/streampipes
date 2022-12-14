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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class AdapterResourceManager extends AbstractResourceManager<IAdapterStorage> {

  public AdapterResourceManager(IAdapterStorage adapterStorage) {
    super(adapterStorage);
  }

  public AdapterResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage());
  }

  /**
   * Takes an {@link AdapterDescription}, encrypts the password properties and stores it to the database
   *
   * @param adapterDescription input adapter description
   * @return the id of the created adapter
   */
  public String encryptAndCreate(AdapterDescription adapterDescription) {
    AdapterDescription encryptedAdapterDescription = cloneAndEncrypt(adapterDescription);
    encryptedAdapterDescription.setRev(null);
    return db.storeAdapter(encryptedAdapterDescription);
  }

  /**
   * Takes an {@link AdapterDescription}, encrypts the password properties and updates the corresponding database entry
   *
   * @param adapterDescription input adapter description
   */
  public void encryptAndUpdate(AdapterDescription adapterDescription) {
    db.updateAdapter(cloneAndEncrypt(adapterDescription));
  }

  public void delete(String elementId) {
    db.deleteAdapter(elementId);
  }

  /**
   * Takes an adapterDescription and returns an encrypted copy
   */
  private AdapterDescription cloneAndEncrypt(AdapterDescription adapterDescription) {
    AdapterDescription encryptedAdapterDescription = new Cloner().adapterDescription(adapterDescription);
    SecretProvider.getEncryptionService().apply(encryptedAdapterDescription);
    return encryptedAdapterDescription;
  }

}

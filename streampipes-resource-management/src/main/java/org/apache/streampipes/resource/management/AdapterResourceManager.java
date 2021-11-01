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

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.resource.utils.AdapterEncryptionService;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;

public class AdapterResourceManager extends AbstractResourceManager<IAdapterStorage> {

  public AdapterResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage());
  }

  public String encryptAndCreate(AdapterDescription ad,
                                 String principalSid,
                                 boolean publicElement) {
    // Encrypt adapter description to store it in db
    AdapterDescription encryptedAdapterDescription =
            new AdapterEncryptionService(new Cloner().adapterDescription(ad)).encrypt();

    // store in db
    encryptedAdapterDescription.setRev(null);
    String elementId = db.storeAdapter(encryptedAdapterDescription);
    //new PermissionResourceManager().createDefault(elementId, AdapterDescription.class, principalSid, publicElement);
    return elementId;
  }

  public void delete(String elementId) {
    AdapterDescription ad = db.getAdapter(elementId);
    //deletePermissions(ad);
    db.deleteAdapter(elementId);
  }

  private void deletePermissions(AdapterDescription description) {
    SpResourceManager manager = new SpResourceManager();
    List<Permission> permissions = manager.managePermissions().findForObjectId(description.getElementId());
    permissions.forEach(permission -> manager.managePermissions().delete(permission));
  }

}

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
import org.apache.streampipes.model.shared.api.Storable;
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.storage.api.CRUDStorage;

import java.util.List;

public abstract class AbstractCRUDResourceManager<T extends Storable>
    extends AbstractResourceManager<CRUDStorage<T>> {

  private final Class<T> elementClass;

  public AbstractCRUDResourceManager(CRUDStorage<T> db,
                                     Class<T> elementClass) {
    super(db);
    this.elementClass = elementClass;
  }

  public List<T> findAll() {
    return db.findAll();
  }

  public T find(String elementId) {
    return db.getElementById(elementId);
  }

  public void delete(String elementId) {
    db.deleteElementById(elementId);
    deletePermissions(elementId);
  }

  public T create(T element,
                     String principalSid) {
    if (element.getElementId() == null) {
      element.setElementId(ElementIdGenerator.makeElementId(elementClass));
    }
    db.persist(element);
    new PermissionResourceManager().createDefault(element.getElementId(), elementClass, principalSid,
        false);
    return find(element.getElementId());
  }

  public void update(T element) {
    db.updateElement(element);
  }

  private void deletePermissions(String elementId) {
    PermissionResourceManager manager = new PermissionResourceManager();
    List<Permission> permissions = manager.findForObjectId(elementId);
    permissions.forEach(manager::delete);
  }
}

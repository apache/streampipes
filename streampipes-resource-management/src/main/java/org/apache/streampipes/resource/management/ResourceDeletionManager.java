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

import org.apache.streampipes.storage.api.core.CRUDStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** Coordinates document deletion and asset-link cleanup for linkable resources. */
public class ResourceDeletionManager {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceDeletionManager.class);

  private final IGenericStorage genericStorage;

  public ResourceDeletionManager(IGenericStorage genericStorage) {
    this.genericStorage = genericStorage;
  }

  public <T> void delete(CRUDStorage<T> storage, String resourceId) {
    var resource = storage.getElementById(resourceId);
    if (resource != null) {
      removeAssetLinks(resourceId);
      storage.deleteElement(resource);
    }
  }

  public void removeAssetLinks(String resourceId) {
    try {
      genericStorage.deleteAssetLinkToResource(resourceId);
    } catch (IOException e) {
      LOG.error("Asset links for {} could not be deleted", resourceId, e);
    }
  }
}

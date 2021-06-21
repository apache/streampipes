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

package org.apache.streampipes.manager.verification;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.model.SpDataStream;

import java.io.IOException;

public class DataStreamVerifier extends ElementVerifier<SpDataStream> {

  public DataStreamVerifier(String graphData) {
    super(graphData, SpDataStream.class);
  }

  public DataStreamVerifier(SpDataStream stream) {
    super(stream);
  }

  @Override
  protected void collectValidators() {
    super.collectValidators();
  }

  @Override
  protected StorageState store(String username, boolean publicElement, boolean refreshCache) {
    StorageState storageState = StorageState.STORED;
		/*
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			StorageManager.INSTANCE.getUserStorageAPI().addSource(username, elementDescription.getElementId());
		}
*/
    if (!storageApi.exists(elementDescription)) {
      storageApi.storeDataStream(elementDescription);
      if (refreshCache) {
        storageApi.refreshDataSourceCache();
      }
    } else {
      storageState = StorageState.ALREADY_IN_SESAME;
    }
    if (!(userService.getOwnSourceUris(username).contains(elementDescription.getUri()))) {
      userService.addOwnSource(username, elementDescription.getUri(), publicElement);
    } else {
      storageState = StorageState.ALREADY_IN_SESAME_AND_USER_DB;
    }
    return storageState;
  }

  @Override
  protected void update(String username) {
    storageApi.update(elementDescription);
    storageApi.refreshDataSourceCache();
  }

  @Override
  protected void storeAssets() throws IOException, NoServiceEndpointsAvailableException {
    if (elementDescription.isIncludesAssets()) {
      AssetManager.storeAsset(SpServiceUrlProvider.DATA_STREAM, elementDescription.getAppId());
    }
  }

  @Override
  protected void updateAssets() throws IOException, NoServiceEndpointsAvailableException {
    if (elementDescription.isIncludesAssets()) {
      AssetManager.deleteAsset(elementDescription.getAppId());
      storeAssets();
    }
  }
}

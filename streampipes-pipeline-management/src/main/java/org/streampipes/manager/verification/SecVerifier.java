/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.verification;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.manager.assets.AssetManager;
import org.streampipes.model.graph.DataSinkDescription;

import java.io.IOException;

public class SecVerifier extends ElementVerifier<DataSinkDescription> {

	
	public SecVerifier(String graphData)
			throws SepaParseException {
		super(graphData, DataSinkDescription.class);
	}


	@Override
	protected StorageState store(String username, boolean publicElement) {
		StorageState storageState = StorageState.STORED;
		/*
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			StorageManager.INSTANCE.getUserStorageAPI().addAction(username, elementDescription.getElementId());
		}
*/
		if (!storageApi.exists(elementDescription)) storageApi.storeSEC(elementDescription);
		else storageState = StorageState.ALREADY_IN_SESAME;
		if (!(userService.getOwnActionUris(username).contains(elementDescription.getUri()))) userService.addOwnAction(username, elementDescription.getUri(), publicElement);
		else storageState = StorageState.ALREADY_IN_SESAME_AND_USER_DB;
		return storageState;
	}

	@Override
	protected void collectValidators() {
		super.collectValidators();
	}


	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}

	@Override
	protected void storeAssets() throws IOException  {
		if (elementDescription.isIncludesAssets()) {
			AssetManager.storeAsset(elementDescription.getElementId(), elementDescription.getAppId());
		}
	}

	@Override
	protected void updateAssets() throws IOException {
		if (elementDescription.isIncludesAssets()) {
			AssetManager.deleteAsset(elementDescription.getAppId());
			storeAssets();
		}
	}
}

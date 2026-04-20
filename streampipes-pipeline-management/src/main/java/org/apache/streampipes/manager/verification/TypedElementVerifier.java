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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TypedElementVerifier<T extends NamedStreamPipesEntity> extends ElementVerifier<T> {

  private final Predicate<T> existsChecker;
  private final Consumer<T> storeOperation;
  private final Consumer<T> updateOperation;
  private final SpServiceUrlProvider serviceUrlProvider;
  private final ExtensionServiceRequestManager requestManager;

  public TypedElementVerifier(
      String graphData,
      Class<T> elementClass,
      IPipelineElementDescriptionStorage storageApi,
      Predicate<T> existsChecker,
      Consumer<T> storeOperation,
      Consumer<T> updateOperation,
      SpServiceUrlProvider serviceUrlProvider,
      ExtensionServiceRequestManager requestManager
  ) {
    super(graphData, elementClass, storageApi);
    this.existsChecker = existsChecker;
    this.storeOperation = storeOperation;
    this.updateOperation = updateOperation;
    this.serviceUrlProvider = serviceUrlProvider;
    this.requestManager = requestManager;
  }

  public TypedElementVerifier(
      T elementDescription,
      IPipelineElementDescriptionStorage storageApi,
      Predicate<T> existsChecker,
      Consumer<T> storeOperation,
      Consumer<T> updateOperation,
      SpServiceUrlProvider serviceUrlProvider,
      ExtensionServiceRequestManager requestManager
  ) {
    super(elementDescription, storageApi);
    this.existsChecker = existsChecker;
    this.storeOperation = storeOperation;
    this.updateOperation = updateOperation;
    this.serviceUrlProvider = serviceUrlProvider;
    this.requestManager = requestManager;
  }

  @Override
  protected StorageState store() {
    if (!existsChecker.test(elementDescription)) {
      storeOperation.accept(elementDescription);
      return StorageState.STORED;
    }
    return StorageState.ALREADY_STORED;
  }

  @Override
  protected void update() {
    updateOperation.accept(elementDescription);
  }

  @Override
  protected void storeAssets() throws IOException, NoServiceEndpointsAvailableException {
    if (elementDescription.isIncludesAssets()) {
      AssetManager.storeAsset(serviceUrlProvider, elementDescription.getAppId(), requestManager);
    }
  }
}

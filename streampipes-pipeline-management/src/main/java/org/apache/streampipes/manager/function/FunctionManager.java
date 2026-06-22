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

package org.apache.streampipes.manager.function;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.function.FunctionState;
import org.apache.streampipes.model.function.FunctionsShutdownResponse;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.function.IFunctionStateStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FunctionManager {

  private static final Logger LOG = LoggerFactory.getLogger(FunctionManager.class);

  private final ExtensionServiceRequestManager requestManager;
  private final SpResourceManager resourceManager;

  public FunctionManager(ExtensionServiceRequestManager requestManager,
                         SpResourceManager resourceManager) {
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
  }

  public void stopAllFunctionsAndPersistState(IFunctionStateStorage functionStateStorage) {
    var extensions = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage().findAll();

    LOG.info("Triggering function stop at {} extension services...", extensions.size());
    extensions.forEach(service -> {
      var shutdownResponse = triggerFunctionStop(service);
      if (shutdownResponse != null) {
        persistReturnedFunctionStates(functionStateStorage, shutdownResponse);
      }
    });
  }

  private FunctionsShutdownResponse triggerFunctionStop(SpServiceRegistration service) {
    var requestTarget = ExtensionServiceRequestTargets.functionStop(service);

    try {
      LOG.info("Triggering function stop at {}", requestTarget.baseUrl());
      var response = requestManager.request(ExtensionServiceRequests.functionStop(requestTarget, resourceManager));
      int statusCode = response.statusCode();

      if (statusCode >= 200 && statusCode < 300) {
        LOG.debug("Function stop triggered at {} (HTTP {})", service.getSvcId(), statusCode);
        if (response.responseBody() == null) {
          return null;
        }

        return JacksonSerializer.getObjectMapper().readValue(
            response.responseBody(),
            FunctionsShutdownResponse.class
        );
      } else {
        LOG.warn("Function stop request returned non-success status at {} (HTTP {})",
            service.getSvcId(), statusCode);
        return null;
      }
    } catch (IOException e) {
      LOG.warn("Could not trigger function stop at {}: {}", requestTarget.baseUrl(), e.getMessage());
      return null;
    }
  }

  private void persistReturnedFunctionStates(IFunctionStateStorage functionStateStorage,
                                                    FunctionsShutdownResponse shutdownResponse) {
    if (shutdownResponse == null || shutdownResponse.getFunctions() == null) {
      return;
    }

    shutdownResponse.getFunctions().forEach(functionResult -> {
      if (functionResult.getState() != null) {
        var existingFunctionState = functionStateStorage.getElementById(functionResult.getFunctionId());
        if (existingFunctionState != null) {
          existingFunctionState.setState(functionResult.getState());
          functionStateStorage.updateElement(existingFunctionState);
        } else {
          functionStateStorage.persist(new FunctionState(functionResult.getFunctionId(), functionResult.getState()));
        }
      }
    });
  }
}

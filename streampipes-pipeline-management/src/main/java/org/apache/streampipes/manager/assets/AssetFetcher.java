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
package org.apache.streampipes.manager.assets;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.param.PipelineElementAssetParameters;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class AssetFetcher {

  private final SpServiceUrlProvider spServiceUrlProvider;
  private final String appId;
  private final ExtensionServiceRequestManager requestManager;

  public AssetFetcher(SpServiceUrlProvider spServiceUrlProvider,
                      String appId,
                      ExtensionServiceRequestManager requestManager) {
    this.spServiceUrlProvider = spServiceUrlProvider;
    this.appId = appId;
    this.requestManager = requestManager;
  }

  public InputStream fetchPipelineElementAssets() throws IOException, NoServiceEndpointsAvailableException {
    var service = new ExtensionsServiceEndpointGenerator().selectService(appId, spServiceUrlProvider, Set.of());
    var requestTarget = new ExtensionServiceRequestTarget(
        service.getServiceUrl(),
        service.getSvcId(),
        new PipelineElementAssetParameters(spServiceUrlProvider, appId)
    );
    var response = requestManager.requestPipelineElementAssets(requestTarget);

    if (!response.isSuccess()) {
      throw new IOException("Could not fetch pipeline element assets from " + service.getSvcGroup());
    }

    var responseBytes = response.responseBytes();
    return new ByteArrayInputStream(responseBytes == null ? new byte[0] : responseBytes);

  }
}

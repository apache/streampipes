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
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.InputStream;

public class AssetFetcher {

  private static final String ASSET_ENDPOINT_APPENDIX = "/assets";

  private SpServiceUrlProvider spServiceUrlProvider;
  private String appId;

  public AssetFetcher(SpServiceUrlProvider spServiceUrlProvider,
                      String appId) {
    this.spServiceUrlProvider = spServiceUrlProvider;
    this.appId = appId;
  }

  public InputStream fetchPipelineElementAssets() throws IOException, NoServiceEndpointsAvailableException {
    String endpointUrl = new ExtensionsServiceEndpointGenerator(appId, spServiceUrlProvider).getEndpointResourceUrl();
    return Request
        .Get(endpointUrl + ASSET_ENDPOINT_APPENDIX)
        .execute()
        .returnContent()
        .asStream();

  }
}

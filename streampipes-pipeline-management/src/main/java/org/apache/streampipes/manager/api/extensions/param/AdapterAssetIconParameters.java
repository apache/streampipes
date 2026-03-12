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

package org.apache.streampipes.manager.api.extensions.param;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationType;

import java.util.List;

public class AdapterAssetIconParameters implements ExtensionServiceOperationParameters {

  private final String appId;

  public AdapterAssetIconParameters(String appId) {
    this.appId = appId;
  }

  @Override
  public String toUrl(String baseUrl) {
    return String.join("/",
        List.of(baseUrl, "api", "v1", "worker", "adapters", appId, "assets", "icon"));
  }

  @Override
  public String toTopic(String topicPrefix) {
    return "";
  }

  @Override
  public ExtensionServiceOperationType getOperationType() {
    return ExtensionServiceOperationType.ADAPTER_ICON_ASSET;
  }
}

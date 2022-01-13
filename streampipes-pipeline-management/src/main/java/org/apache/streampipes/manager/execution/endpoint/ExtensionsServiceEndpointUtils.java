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
package org.apache.streampipes.manager.execution.endpoint;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.NoSuchElementException;

public class ExtensionsServiceEndpointUtils {

  public static SpServiceUrlProvider getPipelineElementType(NamedStreamPipesEntity entity) {
    return isDataProcessor(entity) ? SpServiceUrlProvider.DATA_PROCESSOR : SpServiceUrlProvider.DATA_SINK;
  }

  public static SpServiceUrlProvider getPipelineElementType(String appId) {
    try {
      StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineElementDescriptionStorage().getDataProcessorByAppId(appId);
      return SpServiceUrlProvider.DATA_PROCESSOR;
    } catch (NoSuchElementException e) {
      return SpServiceUrlProvider.DATA_SINK;
    }
  }

  private static boolean isDataProcessor(NamedStreamPipesEntity entity) {
    return entity instanceof DataProcessorInvocation || entity instanceof DataProcessorDescription;
  }
}

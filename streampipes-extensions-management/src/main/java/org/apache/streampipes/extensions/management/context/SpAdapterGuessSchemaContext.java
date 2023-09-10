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

package org.apache.streampipes.extensions.management.context;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.api.config.IConfigExtractor;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;

public class SpAdapterGuessSchemaContext implements IAdapterGuessSchemaContext {

  protected IConfigExtractor configExtractor;
  protected StreamPipesClient streamPipesClient;

  public SpAdapterGuessSchemaContext(IConfigExtractor configExtractor,
                                     StreamPipesClient streamPipesClient) {
    this.configExtractor = configExtractor;
    this.streamPipesClient = streamPipesClient;
  }

  @Override
  public IConfigExtractor getConfigStore() {
    return configExtractor;
  }

  @Override
  public StreamPipesClient getStreamPipesClient() {
    return streamPipesClient;
  }
}

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
package org.apache.streampipes.processors.transformation.flink;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.processors.transformation.flink.config.ConfigKeys;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class AbstractFlinkTransformationProgram<T extends EventProcessorBindingParams>
    extends FlinkDataProcessorRuntime<T> {

  public AbstractFlinkTransformationProgram(T params,
                                            ConfigExtractor configExtractor,
                                            StreamPipesClient streamPipesClient) {
    super(params, configExtractor, streamPipesClient);
  }

  @Override
  protected FlinkDeploymentConfig getDeploymentConfig(ConfigExtractor configExtractor) {
    SpConfig config = configExtractor.getConfig();
    return new FlinkDeploymentConfig(config.getString(
        ConfigKeys.FLINK_JAR_FILE_LOC),
        config.getString(ConfigKeys.FLINK_HOST),
        config.getInteger(ConfigKeys.FLINK_PORT),
        config.getBoolean(ConfigKeys.DEBUG)
    );
  }


}

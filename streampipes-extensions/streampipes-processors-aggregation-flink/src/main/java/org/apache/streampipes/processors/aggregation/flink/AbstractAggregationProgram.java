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
package org.apache.streampipes.processors.aggregation.flink;

import org.apache.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class AbstractAggregationProgram<B extends EventProcessorBindingParams> extends FlinkDataProcessorRuntime<B> {

  public AbstractAggregationProgram(B params, boolean debug) {
    super(params, debug);
  }

  public AbstractAggregationProgram(B params) {
    super(params, false);
  }

  @Override
  protected FlinkDeploymentConfig getDeploymentConfig() {
    return new FlinkDeploymentConfig(AggregationFlinkConfig.JAR_FILE,
            AggregationFlinkConfig.INSTANCE.getFlinkHost(), AggregationFlinkConfig.INSTANCE.getFlinkPort());
  }

}


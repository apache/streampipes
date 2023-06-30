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

package org.apache.streampipes.wrapper.flink;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

public abstract class FlinkDataSinkDeclarer<T extends SinkParams>
    implements IStreamPipesDataSink {

  @Override
  public void onPipelineStarted(IDataSinkParameters params, EventSinkRuntimeContext runtimeContext) {

  }

  @Override
  public void onEvent(Event event) {

  }

  @Override
  public void onPipelineStopped() {

  }

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        () -> this,
        declareModel()
    );
  }

  public abstract FlinkDataSinkProgram<T> getProgram(DataSinkInvocation graph,
                                                     IDataSinkParameterExtractor extractor);

  public abstract DataSinkDescription declareModel();
}

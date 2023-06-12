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
package org.apache.streampipes.wrapper.standalone;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

public abstract class StreamPipesDataSink implements IStreamPipesDataSink {

  @Override
  public void onPipelineStarted(IDataSinkParameters params,
                                EventSinkRuntimeContext runtimeContext) {
    SinkParams parameters = new SinkParams(params);
    this.onInvocation(parameters, runtimeContext);
  }

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        () -> {
          try {
            return this.getClass().newInstance();
          } catch (InstantiationException e) {
            throw new RuntimeException(e);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        },
        declareModel()
    );
  }

  @Override
  public void onPipelineStopped() {
    this.onDetach();
  }

  public abstract DataSinkDescription declareModel();

  public abstract void onInvocation(SinkParams parameters,
                                    EventSinkRuntimeContext runtimeContext) throws SpRuntimeException;

  public abstract void onDetach();
}

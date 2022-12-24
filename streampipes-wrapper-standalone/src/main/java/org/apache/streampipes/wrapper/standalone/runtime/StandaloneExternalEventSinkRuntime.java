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
package org.apache.streampipes.wrapper.standalone.runtime;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.apache.streampipes.wrapper.runtime.ExternalEventSink;

import java.util.Map;
import java.util.function.Supplier;

public class StandaloneExternalEventSinkRuntime<T extends EventSinkBindingParams> extends
    StandalonePipelineElementRuntime<T, DataSinkInvocation,
        EventSinkRuntimeParams<T>, EventSinkRuntimeContext, ExternalEventSink<T>> {

  public StandaloneExternalEventSinkRuntime(Supplier<ExternalEventSink<T>> supplier,
                                            EventSinkRuntimeParams<T> runtimeParams) {
    super(supplier, runtimeParams);
  }

  @Override
  public void bindEngine() throws SpRuntimeException {
    engine.onInvocation(params.getBindingParams(), params.getRuntimeContext());
  }

  @Override
  public void process(Map<String, Object> rawEvent, String sourceInfo) {

  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {

  }

  @Override
  public void postDiscard() throws SpRuntimeException {

  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    bindEngine();
  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    discardEngine();
    postDiscard();
  }
}

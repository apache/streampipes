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
import org.apache.streampipes.extensions.management.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.wrapper.context.RuntimeContext;
import org.apache.streampipes.wrapper.params.binding.BindingParams;
import org.apache.streampipes.wrapper.params.runtime.RuntimeParams;
import org.apache.streampipes.wrapper.routing.RawDataProcessor;
import org.apache.streampipes.wrapper.routing.SpInputCollector;
import org.apache.streampipes.wrapper.runtime.PipelineElement;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class StandalonePipelineElementRuntime<T extends BindingParams<K>,
    K extends InvocableStreamPipesEntity,
    V extends RuntimeParams<T, K, X>,
    X extends RuntimeContext,
    PeT extends PipelineElement<T, K>>
    extends PipelineElementRuntime implements RawDataProcessor {

  protected final PeT engine;
  protected V params;
  protected SpMonitoringManager monitoringManager;
  protected String resourceId;

  public StandalonePipelineElementRuntime(Supplier<PeT> supplier, V runtimeParams) {
    super();
    this.engine = supplier.get();
    this.params = runtimeParams;
    this.monitoringManager = params.getRuntimeContext().getLogger();
    this.resourceId = params.getBindingParams().getGraph().getElementId();
  }

  public PeT getEngine() {
    return engine;
  }

  public void discardEngine() throws SpRuntimeException {
    engine.onDetach();
    this.monitoringManager.resetCounter(resourceId);
  }

  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : params.getBindingParams().getGraph().getInputStreams()) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
              .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0),
          params.isSingletonEngine()));
    }
    return inputCollectors;
  }

  protected void addLogEntry(RuntimeException e) {
    monitoringManager.addErrorMessage(
        params.getBindingParams().getGraph().getElementId(),
        SpLogEntry.from(System.currentTimeMillis(), StreamPipesErrorMessage.from(e)));
  }

  public abstract void bindEngine() throws SpRuntimeException;


}

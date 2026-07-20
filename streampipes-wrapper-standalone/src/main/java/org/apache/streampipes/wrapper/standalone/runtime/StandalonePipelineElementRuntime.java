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
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.context.IContextGenerator;
import org.apache.streampipes.extensions.api.pe.context.RuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IInternalRuntimeParameters;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.extensions.api.pe.routing.PipelineElementCollector;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.wrapper.params.InternalRuntimeParameters;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;

public abstract class StandalonePipelineElementRuntime<
    PeT extends IStreamPipesPipelineElement<?>,
    IvT extends InvocableStreamPipesEntity,
    RcT extends RuntimeContext,
    ExT extends IParameterExtractor,
    PepT extends IPipelineElementParameters<IvT, ExT>>
    extends PipelineElementRuntime<PeT, IvT, RcT, ExT, PepT> implements RawDataProcessor {

  protected List<SpInputCollector> inputCollectors;

  protected String instanceId;
  protected PepT runtimeParameters;
  protected RcT runtimeContext;

  protected PeT pipelineElement;
  protected IInternalRuntimeParameters internalRuntimeParameters;

  protected final SpMonitoringManager monitoringManager;

  public StandalonePipelineElementRuntime(IContextGenerator<RcT, IvT> contextGenerator,
                                          IParameterGenerator<IvT, ExT, PepT> parameterGenerator) {
    super(contextGenerator, parameterGenerator);
    this.internalRuntimeParameters = new InternalRuntimeParameters();
    this.monitoringManager = SpMonitoringManager.INSTANCE;
  }

  @Override
  public void startRuntime(IvT pipelineElementInvocation,
                           PeT pipelineElement,
                           PepT runtimeParameters,
                           RcT runtimeContext) {
    this.pipelineElement = pipelineElement;
    this.runtimeParameters = runtimeParameters;
    this.runtimeContext = runtimeContext;
    this.instanceId = pipelineElementInvocation.getElementId();
    this.inputCollectors = getInputCollectors(pipelineElementInvocation.getInputStreams());
    try {
      this.beforeStart();
    } catch (RuntimeException e) {
      try {
        this.afterStartFailed();
      } catch (RuntimeException cleanupException) {
        e.addSuppressed(cleanupException);
      }
      throw e;
    }
  }

  @Override
  public void stopRuntime() {
    RuntimeException stopException = null;
    try {
      unregisterInputCollectors();
    } catch (RuntimeException e) {
      stopException = collectCleanupException(stopException, e);
    }
    try {
      afterStop();
    } catch (RuntimeException e) {
      stopException = collectCleanupException(stopException, e);
    }
    try {
      removeMonitoring(instanceId);
    } catch (RuntimeException e) {
      stopException = collectCleanupException(stopException, e);
    }

    if (stopException != null) {
      throw stopException;
    }
  }

  protected void removeMonitoring(String resourceId) throws SpRuntimeException {
    monitoringManager.remove(resourceId);
  }

  protected List<SpInputCollector> getInputCollectors(List<SpDataStream> inputStreams) throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : inputStreams) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
              .getTransportProtocol(),
          false));
    }
    return inputCollectors;
  }

  protected void addLogEntry(RuntimeException e) {
    runtimeContext.getLogger().error(e);
  }

  protected void connectInputCollectors() {
    inputCollectors.forEach(PipelineElementCollector::connect);
  }

  protected void disconnectInputCollectors() {
    inputCollectors.forEach(PipelineElementCollector::disconnect);
  }

  protected void registerInputCollectors() {
    this.inputCollectors.forEach(is -> is.registerConsumer(instanceId, this));
  }

  protected void unregisterInputCollectors() {
    if (this.inputCollectors != null) {
      this.inputCollectors.forEach(is -> is.unregisterConsumer(instanceId));
    }
  }

  protected void afterStartFailed() {
    RuntimeException cleanupException = null;
    try {
      unregisterInputCollectors();
    } catch (RuntimeException e) {
      cleanupException = collectCleanupException(cleanupException, e);
    }
    try {
      disconnectInputCollectors();
    } catch (RuntimeException e) {
      cleanupException = collectCleanupException(cleanupException, e);
    }
    try {
      removeMonitoring(instanceId);
    } catch (RuntimeException e) {
      cleanupException = collectCleanupException(cleanupException, e);
    }

    if (cleanupException != null) {
      throw cleanupException;
    }
  }

  protected RuntimeException collectCleanupException(RuntimeException cleanupException,
                                                    RuntimeException nextException) {
    if (cleanupException == null) {
      return nextException;
    } else {
      cleanupException.addSuppressed(nextException);
      return cleanupException;
    }
  }

  protected abstract void beforeStart();

  protected abstract void afterStop();
}

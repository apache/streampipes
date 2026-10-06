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
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.params.InternalRuntimeParameters;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class StandalonePipelineElementRuntime<
    PeT extends IStreamPipesPipelineElement<?>,
    IvT extends InvocableStreamPipesEntity,
    RcT extends RuntimeContext,
    ExT extends IParameterExtractor,
    PepT extends IPipelineElementParameters<IvT, ExT>>
    extends PipelineElementRuntime<PeT, IvT, RcT, ExT, PepT> implements RawDataProcessor {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private boolean pipelineStarted;

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
    try {
      this.inputCollectors = getInputCollectors(pipelineElementInvocation.getInputStreams());
      this.beforeStart();
    } catch (RuntimeException e) {
      try {
        this.afterStartFailed();
      } catch (RuntimeException cleanupException) {
        collectCleanupException(e, cleanupException);
      }
      throw e;
    }
  }

  @Override
  public void process(Map<String, Object> rawEvent, long size, String sourceInfo) {
    try {
      monitoringManager.increaseInCounter(instanceId, sourceInfo, size, System.currentTimeMillis());
      processEvent(internalRuntimeParameters.makeEvent(runtimeParameters, rawEvent, sourceInfo));
    } catch (RuntimeException e) {
      handleProcessingException(e);
    }
  }

  /**
   * Typed dispatch hook for the shared processing path. Existing runtimes may
   * continue to override process directly.
   */
  protected void processEvent(Event event) {
    throw new UnsupportedOperationException("Runtime must implement event dispatch");
  }

  protected void handleProcessingException(RuntimeException e) {
    log.error("RuntimeException while processing event in {}", pipelineElement.getClass().getCanonicalName(), e);
    addLogEntry(e);
  }

  protected void startPipeline(Runnable startCallback) {
    startCallback.run();
    pipelineStarted = true;
  }

  protected void stopPipeline(Runnable stopCallback) {
    if (pipelineStarted) {
      pipelineStarted = false;
      stopCallback.run();
    }
  }

  @Override
  public void stopRuntime() {
    cleanupRuntime();
  }

  private void cleanupRuntime() {
    runCleanup(this::unregisterInputCollectors, this::afterStop, () -> removeMonitoring(instanceId));
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
    cleanupInputCollectors(PipelineElementCollector::disconnect);
  }

  protected void registerInputCollectors() {
    this.inputCollectors.forEach(is -> is.registerConsumer(instanceId, this));
  }

  protected void unregisterInputCollectors() {
    cleanupInputCollectors(collector -> collector.unregisterConsumer(instanceId));
  }

  private void cleanupInputCollectors(Consumer<SpInputCollector> cleanup) {
    if (inputCollectors != null) {
      runCleanup(inputCollectors.stream()
          .<Runnable>map(collector -> () -> cleanup.accept(collector))
          .toArray(Runnable[]::new));
    }
  }

  protected void afterStartFailed() {
    cleanupRuntime();
  }

  protected void runCleanup(Runnable... actions) {
    RuntimeException failure = null;
    for (Runnable action : actions) {
      try {
        action.run();
      } catch (RuntimeException e) {
        failure = collectCleanupException(failure, e);
      }
    }
    if (failure != null) {
      throw failure;
    }
  }

  protected RuntimeException collectCleanupException(RuntimeException cleanupException,
                                                    RuntimeException nextException) {
    if (cleanupException == null) {
      return nextException;
    }
    if (cleanupException != nextException) {
      cleanupException.addSuppressed(nextException);
    }
    return cleanupException;
  }

  protected abstract void beforeStart();

  protected abstract void afterStop();
}

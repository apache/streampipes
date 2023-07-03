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

package org.apache.streampipes.wrapper.runtime;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.context.IContextGenerator;
import org.apache.streampipes.extensions.api.pe.context.RuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

public abstract class PipelineElementRuntime<
    PeT extends IStreamPipesPipelineElement<?>,
    IvT extends InvocableStreamPipesEntity,
    RcT extends RuntimeContext,
    ExT extends IParameterExtractor<IvT>,
    PepT extends IPipelineElementParameters<IvT, ExT>>
    implements IStreamPipesRuntime<PeT, IvT> {

  protected String elementId;

  IContextGenerator<RcT, IvT> contextGenerator;
  IParameterGenerator<IvT, ExT, PepT> parameterGenerator;

  public PipelineElementRuntime(IContextGenerator<RcT, IvT> contextGenerator,
                                IParameterGenerator<IvT, ExT, PepT> parameterGenerator) {
    this.contextGenerator = contextGenerator;
    this.parameterGenerator = parameterGenerator;
  }

  @Override
  public Response onRuntimeInvoked(String instanceId,
                                   PeT pipelineElement,
                                   IvT pipelineElementInvocation) {
    try {
      var context = contextGenerator.makeContext(pipelineElementInvocation);
      var parameters = parameterGenerator.makeParameters(pipelineElementInvocation);
      elementId = pipelineElementInvocation.getElementId();
      startRuntime(pipelineElementInvocation, pipelineElement, parameters, context);
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  @Override
  public Response onRuntimeDetached(String instanceId) {
    try {
      stopRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  public abstract void startRuntime(IvT pipelineElementInvocation,
                                    PeT pipelineElement,
                                    PepT runtimeParameters,
                                    RcT runtimeContext);

  public abstract void stopRuntime();

}

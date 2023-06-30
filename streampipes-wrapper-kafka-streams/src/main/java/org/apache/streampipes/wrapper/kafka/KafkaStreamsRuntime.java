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
package org.apache.streampipes.wrapper.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.context.IContextGenerator;
import org.apache.streampipes.extensions.api.pe.context.RuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.apache.streampipes.wrapper.params.InternalRuntimeParameters;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public abstract class KafkaStreamsRuntime<
    PeT extends IStreamPipesPipelineElement<?>,
    IvT extends InvocableStreamPipesEntity,
    RcT extends RuntimeContext,
    ExT extends IParameterExtractor<IvT>,
    PepT extends IPipelineElementParameters<IvT, ExT>>
    extends DistributedRuntime<PeT, IvT, RcT, ExT, PepT>
    implements IStreamPipesRuntime<PeT, IvT> {

  Properties config;
  KafkaStreams streams;

  protected IvT pipelineElementInvocation;
  protected PeT pipelineElement;
  protected PepT runtimeParameters;

  protected RcT runtimeContext;

  protected InternalRuntimeParameters internalRuntimeParameters;

  public KafkaStreamsRuntime(IContextGenerator<RcT, IvT> contextGenerator,
                             IParameterGenerator<IvT, ExT, PepT> parameterGenerator) {
    super(contextGenerator, parameterGenerator);
  }

  @Override
  public void startRuntime(IvT pipelineElementInvocation,
                           PeT pipelineElement,
                           PepT runtimeParameters,
                           RcT runtimeContext) {
    this.pipelineElementInvocation = pipelineElementInvocation;
    this.pipelineElement = pipelineElement;
    this.runtimeParameters = runtimeParameters;
    this.runtimeContext = runtimeContext;
    this.internalRuntimeParameters = new InternalRuntimeParameters();
    prepareRuntime();
    bindRuntime();
  }

  @Override
  public void stopRuntime() {
    streams.close();
    afterStop();
  }

  public void prepareRuntime() throws SpRuntimeException {
    config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, gneerateApplicationId(runtimeParameters
        .getModel()
        .getElementId()));
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaUrl(runtimeParameters
        .getModel()
        .getInputStreams().get(0)));
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
  }

  private String gneerateApplicationId(String elementId) {
    return elementId.replaceAll("/", "-").replaceAll(":", "-");
  }

  protected abstract void bindRuntime();

  protected abstract void afterStop();
}

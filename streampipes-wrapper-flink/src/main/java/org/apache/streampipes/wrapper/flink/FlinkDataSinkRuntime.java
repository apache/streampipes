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
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IDataSinkRuntime;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.context.generator.DataSinkContextGenerator;
import org.apache.streampipes.wrapper.params.generator.DataSinkParameterGenerator;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlinkDataSinkRuntime extends FlinkRuntime<
    IStreamPipesDataSink,
    DataSinkInvocation,
    EventSinkRuntimeContext,
    IDataSinkParameterExtractor,
    IDataSinkParameters,
    IDataSinkProgram> implements IDataSinkRuntime {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(FlinkDataSinkRuntime.class);

  public FlinkDataSinkRuntime() {
    super(new DataSinkContextGenerator(), new DataSinkParameterGenerator());
  }


  @Override
  public void appendExecutionConfig(IDataSinkProgram program,
                                    DataStream<Event>... convertedStream) {
    program.getSink(convertedStream);

  }

  @Override
  protected IDataSinkProgram getFlinkProgram(IStreamPipesDataSink pipelineElement) {
    IDataSinkProgram program;

    if (pipelineElement instanceof FlinkDataSinkDeclarer<?>) {
      program = ((FlinkDataSinkDeclarer<?>) pipelineElement)
          .getProgram(
              runtimeParameters.getModel(),
              DataSinkParameterExtractor.from(runtimeParameters.getModel())
          );
    } else {
      program = new FlinkDataSinkCompatProgram(pipelineElement);
    }
    return program;
  }
}

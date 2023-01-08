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
package org.apache.streampipes.rest.impl.dashboard;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractPipelineExtractionResource<T> extends AbstractRestResource {

  protected Response getPipelineByIdAndFieldValue(String appId,
                                                  String pipelineId,
                                                  String fieldValue) {
    List<T> pipelines = extract(new ArrayList<>(), appId);

    Optional<T> matchedPipeline =
        pipelines
            .stream()
            .filter(pipeline -> matches(pipeline, pipelineId, fieldValue)).findFirst();

    return matchedPipeline.isPresent() ? ok(matchedPipeline.get()) : fail();
  }

  protected List<T> extract(List<T> target, String appId) {
    getPipelineStorage()
        .getAllPipelines()
        .forEach(pipeline -> {
          List<DataSinkInvocation> sinks = extractSink(pipeline, appId);
          sinks.forEach(sink -> target.add(convert(pipeline, sink)));
        });
    return target;
  }

  protected abstract T convert(Pipeline pipeline,
                               DataSinkInvocation sink);

  protected abstract boolean matches(T resourceToExtract,
                                     String pipelineId,
                                     String fieldValue);

  protected List<DataSinkInvocation> extractSink(Pipeline pipeline, String appId) {
    return pipeline
        .getActions()
        .stream()
        .filter(sink -> sink.getAppId().equals(appId))
        .collect(Collectors.toList());
  }

  protected String extractFieldValue(DataSinkInvocation sink, String fieldName) {
    return sink.getStaticProperties()
        .stream()
        .filter(sp -> sp.getInternalName().equals(fieldName))
        .map(sp -> (FreeTextStaticProperty) sp)
        .findFirst().get().getValue();
  }

  protected String extractInputTopic(DataSinkInvocation sink) {
    return sink
        .getInputStreams()
        .get(0)
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }
}

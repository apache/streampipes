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

package org.apache.streampipes.wrapper.declarer;

import org.apache.streampipes.extensions.api.declarer.SemanticEventConsumerDeclarer;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class EventSinkDeclarer<K extends EventSinkBindingParams, V extends
    PipelineElementRuntime>
    extends PipelineElementDeclarer<K, V, DataSinkInvocation,
    DataSinkParameterExtractor> implements SemanticEventConsumerDeclarer {

  @Override
  protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
    return DataSinkParameterExtractor.from(graph);
  }

  @Override
  public Response invokeRuntime(DataSinkInvocation graph, String serviceId) {
    return invokeEPRuntime(graph, serviceId);
  }

}

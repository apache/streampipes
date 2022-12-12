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
package org.apache.streampipes.manager.matching;

import org.apache.streampipes.manager.matching.v2.ElementVerification;
import org.apache.streampipes.manager.util.TreeUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.List;

public class ConnectionValidator {

  private final Pipeline pipeline;
  private final List<InvocableStreamPipesEntity> invocationGraphs;
  private final InvocableStreamPipesEntity rootPipelineElement;
  private final ElementVerification verifier;

  public ConnectionValidator(Pipeline pipeline,
                             List<InvocableStreamPipesEntity> invocationGraphs,
                             InvocableStreamPipesEntity rootPipelineElement) {
    this.pipeline = pipeline;
    this.invocationGraphs = invocationGraphs;
    this.rootPipelineElement = rootPipelineElement;
    this.verifier = new ElementVerification();
  }

  public List<InvocableStreamPipesEntity> validateConnection() throws InvalidConnectionException {
    boolean verified = true;
    InvocableStreamPipesEntity rightElement = rootPipelineElement;
    List<String> connectedTo = rootPipelineElement.getConnectedTo();

    for (String domId : connectedTo) {
      NamedStreamPipesEntity element = TreeUtils.findSEPAElement(domId, pipeline.getSepas(), pipeline.getStreams());
      if (element instanceof SpDataStream) {
        SpDataStream leftSpDataStream = (SpDataStream) element;
        if (!(verifier.verify(leftSpDataStream, rightElement))) {
          verified = false;
        }
      } else {
        DataProcessorInvocation ancestor = findInvocationGraph(invocationGraphs, element.getDom());
        if (!(verifier.verify(ancestor, rightElement))) {
          verified = false;
        }
      }
    }
    if (!verified) {
      throw new InvalidConnectionException(verifier.getErrorLog());
    }

    return invocationGraphs;
  }

  private DataProcessorInvocation findInvocationGraph(List<InvocableStreamPipesEntity> graphs, String domId) {
    return (DataProcessorInvocation) TreeUtils.findByDomId(domId, graphs);
  }
}

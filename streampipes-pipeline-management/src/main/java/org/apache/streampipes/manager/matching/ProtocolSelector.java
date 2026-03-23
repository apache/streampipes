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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.manager.util.GroundingUtils;
import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Set;

public class ProtocolSelector {

  private final Environment env;
  private final String outputTopic;
  private final String prioritizedProtocol;
  protected NamedStreamPipesEntity source;
  protected Set<InvocableStreamPipesEntity> targets;

  public ProtocolSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
    this.env = Environments.getEnvironment();
    this.source = source;
    this.targets = targets;
    this.outputTopic = TopicGenerator.generateRandomTopic();


    this.prioritizedProtocol = env.getPrioritizedProtocol().getValueOrDefault();
  }

  public TransportProtocol getPreferredProtocol() {
    var env = Environments.getEnvironment();
    if (source instanceof SpDataStream) {
      return ((SpDataStream) source)
          .getEventGrounding()
          .getTransportProtocol();
    } else {
      return GroundingUtils.makeProtocol(env, prioritizedProtocol, outputTopic);
    }
  }
}

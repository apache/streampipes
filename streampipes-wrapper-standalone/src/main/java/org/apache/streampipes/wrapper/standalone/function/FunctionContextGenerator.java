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

package org.apache.streampipes.wrapper.standalone.function;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.SpDataStream;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionContextGenerator {

  private final List<String> streamIds;
  private final String functionId;
  private final String serviceGroup;

  private final Map<String, SpOutputCollector> outputCollectors;

  public FunctionContextGenerator(String functionId,
                                  String serviceGroup,
                                  List<String> streamIds,
                                  Map<String, SpOutputCollector> outputCollectors) {
    this.streamIds = streamIds;
    this.serviceGroup = serviceGroup;
    this.functionId = functionId;
    this.outputCollectors = outputCollectors;
  }

  public FunctionContext generate() {
    var client = makeClient();
    var streams = receiveStreams(client);

    return new FunctionContext(functionId, serviceGroup, streams, outputCollectors, client);
  }

  private StreamPipesClient makeClient() {
    return new StreamPipesClientResolver().makeStreamPipesClientInstance();
  }

  private List<SpDataStream> receiveStreams(StreamPipesClient client) {
    return this.streamIds
        .stream()
        .map(streamId -> client.streams().get(streamId))
        .flatMap(Optional::stream)
        .toList();
  }
}

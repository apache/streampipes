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

package org.apache.streampipes.manager.template.compact;

import org.apache.streampipes.manager.matching.v2.SchemaMatch;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MatchingStreamFinder {

  public Map<String, List<List<String>>> findMatchedStreams(Pipeline pipeline,
                                                            List<String> requiredStreamInputs,
                                                            List<SpDataStream> allDataStreams) {
    var matchedStreams = new HashMap<String, List<List<String>>>();

    Stream.concat(pipeline.getSepas().stream(), pipeline.getActions().stream())
        .filter(pe -> hasRequiredInputs(pe, requiredStreamInputs))
        .forEach(pe -> matchedStreams.put(
            pe.getElementId(),
            findSupportedStreamsForPe(pe, requiredStreamInputs, allDataStreams))
        );

    return matchedStreams;
  }

  private boolean hasRequiredInputs(InvocableStreamPipesEntity pe, List<String> requiredStreamInputs) {
    return requiredStreamInputs
        .stream()
        .anyMatch(pe.getConnectedTo()::contains);
  }

  private List<List<String>> findSupportedStreamsForPe(InvocableStreamPipesEntity pe,
                                                       List<String> requiredStreamInputs,
                                                       List<SpDataStream> allDataStreams) {
    var streams = new ArrayList<List<String>>();

    for (int i = 0; i < pe.getConnectedTo().size(); i++) {
      String connectedInput = pe.getConnectedTo().get(i);
      if (requiredStreamInputs.contains(connectedInput)) {
        streams.add(getSupportedStreams(allDataStreams, pe.getInputStreams().get(i)));
      }
    }

    return streams;
  }

  private List<String> getSupportedStreams(List<SpDataStream> allDataStreams, SpDataStream inputStreamReq) {
    return allDataStreams.stream()
        .filter(ds -> new SchemaMatch().match(ds.getEventSchema(), inputStreamReq.getEventSchema(), List.of()))
        .map(NamedStreamPipesEntity::getElementId)
        .toList();
  }
}

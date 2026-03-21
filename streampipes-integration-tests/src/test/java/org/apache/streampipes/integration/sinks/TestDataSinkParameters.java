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

package org.apache.streampipes.integration.sinks;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TestDataSinkParameters implements IDataSinkParameters {

  private final DataSinkInvocation model;
  private final IDataSinkParameterExtractor extractor;
  private final List<SourceInfo> inputSourceInfos;
  private final List<SchemaInfo> inputSchemaInfos;
  private final Map<String, Integer> sourceIndexes;

  public TestDataSinkParameters(DataSinkInvocation model, List<SpDataStream> inputStreams) {
    this.model = model;
    this.extractor = DataSinkParameterExtractor.from(model);
    this.inputSourceInfos = IntStream.range(0, inputStreams.size())
        .mapToObj(index -> new SourceInfo("source-" + index, "s" + index))
        .toList();
    this.inputSchemaInfos = IntStream.range(0, inputStreams.size())
        .mapToObj(index -> new SchemaInfo(inputStreams.get(index).getEventSchema(), Collections.emptyList()))
        .toList();
    this.sourceIndexes = IntStream.range(0, inputSourceInfos.size())
        .boxed()
        .collect(java.util.stream.Collectors.toMap(index -> inputSourceInfos.get(index).getSourceId(), index -> index));
  }

  @Override
  public DataSinkInvocation getModel() {
    return model;
  }

  @Override
  public IDataSinkParameterExtractor getExtractor() {
    return extractor;
  }

  @Override
  public IDataSinkParameterExtractor extractor() {
    return extractor;
  }

  @Override
  public List<InputStreamParams> getInputStreamParams() {
    return List.of();
  }

  @Override
  public Map<String, Map<String, Object>> getInEventTypes() {
    return Map.of();
  }

  @Override
  public List<SchemaInfo> getInputSchemaInfos() {
    return inputSchemaInfos;
  }

  @Override
  public List<SourceInfo> getInputSourceInfos() {
    return inputSourceInfos;
  }

  @Override
  public SchemaInfo getInputSchemaInfo(int index) {
    return inputSchemaInfos.get(index);
  }

  @Override
  public SourceInfo getInputSourceInfo(int index) {
    return inputSourceInfos.get(index);
  }

  @Override
  public Integer getSourceIndex(String sourceId) {
    return sourceIndexes.get(sourceId);
  }
}

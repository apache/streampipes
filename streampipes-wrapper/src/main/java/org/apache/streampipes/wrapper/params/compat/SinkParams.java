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
package org.apache.streampipes.wrapper.params.compat;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import java.util.List;
import java.util.Map;

public class SinkParams implements IDataSinkParameters {

  private IDataSinkParameters params;

  private IDataSinkParameterExtractor extractor;
  private DataSinkInvocation graph;

  public SinkParams(IDataSinkParameters params) {
    this(params.getModel());
    this.params = params;
  }

  public SinkParams(DataSinkInvocation graph) {
    this.graph = graph;
    this.extractor = DataSinkParameterExtractor.from(graph);
  }

  @Override
  public IDataSinkParameterExtractor extractor() {
    return extractor;
  }

  @Override
  public DataSinkInvocation getModel() {
    return graph;
  }

  @Override
  public IDataSinkParameterExtractor getExtractor() {
    return extractor;
  }

  @Override
  public List<InputStreamParams> getInputStreamParams() {
    return params.getInputStreamParams();
  }

  @Override
  public Map<String, Map<String, Object>> getInEventTypes() {
    return params.getInEventTypes();
  }

  @Override
  public List<SchemaInfo> getInputSchemaInfos() {
    return params.getInputSchemaInfos();
  }

  @Override
  public List<SourceInfo> getInputSourceInfos() {
    return params.getInputSourceInfos();
  }

  @Override
  public SchemaInfo getInputSchemaInfo(int index) {
    return params.getInputSchemaInfo(index);
  }

  @Override
  public SourceInfo getInputSourceInfo(int index) {
    return params.getInputSourceInfo(index);
  }

  @Override
  public Integer getSourceIndex(String sourceId) {
    return params.getSourceIndex(sourceId);
  }
}

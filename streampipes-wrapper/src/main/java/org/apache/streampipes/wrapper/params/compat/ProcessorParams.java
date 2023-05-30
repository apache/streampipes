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

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;

import java.util.List;
import java.util.Map;

public class ProcessorParams implements IDataProcessorParameters {

  private ProcessingElementParameterExtractor extractor;
  private DataProcessorInvocation graph;

  private IDataProcessorParameters params;

  public ProcessorParams(DataProcessorInvocation graph) {
    this.graph = graph;
    this.extractor = ProcessingElementParameterExtractor.from(graph);
  }

  public ProcessorParams(IDataProcessorParameters params) {
    this(params.getModel());
    this.params = params;
  }

  public ProcessingElementParameterExtractor extractor() {
    return extractor;
  }

  @Override
  public DataProcessorInvocation getModel() {
    return graph;
  }

  @Override
  public IDataProcessorParameterExtractor getExtractor() {
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

  @Override
  public List<PropertyRenameRule> getRenameRules() {
    return params.getRenameRules();
  }

  @Override
  public String getOutName() {
    return params.getOutName();
  }

  public DataProcessorInvocation getGraph() {
    return graph;
  }

  @Override
  public Map<String, Object> getOutEventType() {
    return params.getOutEventType();
  }

  @Override
  public SchemaInfo getOutputSchemaInfo() {
    return params.getOutputSchemaInfo();
  }

  @Override
  public SourceInfo getOutputSourceInfo() {
    return params.getOutputSourceInfo();
  }
}

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

package org.apache.streampipes.wrapper.params;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineElementParameters<T extends InvocableStreamPipesEntity, V extends IParameterExtractor<T>>
    implements IPipelineElementParameters<T, V> {

  private final List<InputStreamParams> inputStreamParams;
  protected final T pipelineElementInvocation;
  private final V parameterExtractor;

  private final Map<String, Map<String, Object>> inEventTypes;

  private final Map<String, Integer> eventInfoMap = new HashMap<>();

  public PipelineElementParameters(T pipelineElementInvocation,
                                   V parameterExtractor,
                                   List<InputStreamParams> params,
                                   Map<String, Map<String, Object>> inEventTypes) {
    this.pipelineElementInvocation = pipelineElementInvocation;
    this.parameterExtractor = parameterExtractor;
    this.inputStreamParams = params;
    this.inEventTypes = inEventTypes;
    buildEventInfoMap();
  }

  private void buildEventInfoMap() {
    for (int i = 0; i < inputStreamParams.size(); i++) {
      String sourceInfo = inputStreamParams.get(i).getSourceInfo()
          .getSourceId();
      eventInfoMap.put(sourceInfo, i);
    }
  }

  @Override
  public List<SourceInfo> getInputSourceInfos() {
    return inputStreamParams.size() == 1 ? Collections.singletonList
        (getInputSourceInfo(0)) : Arrays.asList(getInputSourceInfo(0), getInputSourceInfo(1));
  }

  @Override
  public List<SchemaInfo> getInputSchemaInfos() {
    return inputStreamParams.size() == 1 ? Collections.singletonList
        (getInputSchemaInfo(0)) : Arrays.asList(getInputSchemaInfo(0), getInputSchemaInfo(1));
  }

  @Override
  public SourceInfo getInputSourceInfo(int index) {
    return inputStreamParams.get(index).getSourceInfo();
  }

  @Override
  public Integer getSourceIndex(String sourceId) {
    return eventInfoMap.get(sourceId);
  }

  @Override
  public SchemaInfo getInputSchemaInfo(int index) {
    return inputStreamParams.get(index).getSchemaInfo();
  }

  @Override
  public T getModel() {
    return pipelineElementInvocation;
  }

  @Override
  public V getExtractor() {
    return parameterExtractor;
  }

  @Override
  public V extractor() {
    return parameterExtractor;
  }

  @Override
  public List<InputStreamParams> getInputStreamParams() {
    return inputStreamParams;
  }

  @Override
  public Map<String, Map<String, Object>> getInEventTypes() {
    return inEventTypes;
  }
}

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

package org.apache.streampipes.wrapper.params.runtime;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.wrapper.context.RuntimeContext;
import org.apache.streampipes.wrapper.params.binding.BindingParams;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RuntimeParams<V extends BindingParams<W>, W extends
    InvocableStreamPipesEntity, X extends RuntimeContext> implements Serializable {

  protected final V bindingParams;
  protected X runtimeContext;
  protected ConfigExtractor configExtractor;
  protected StreamPipesClient streamPipesClient;

  private Map<String, Integer> eventInfoMap = new HashMap<>();

  private Boolean singletonEngine;

  public RuntimeParams(V bindingParams,
                       Boolean singletonEngine,
                       ConfigExtractor configExtractor,
                       StreamPipesClient streamPipesClient) {
    this.bindingParams = bindingParams;
    this.singletonEngine = singletonEngine;
    this.configExtractor = configExtractor;
    this.streamPipesClient = streamPipesClient;
    buildEventInfoMap();
    this.runtimeContext = makeRuntimeContext();
  }

  public V getBindingParams() {
    return bindingParams;
  }

  private void buildEventInfoMap() {
    for (int i = 0; i < bindingParams.getInputStreamParams().size(); i++) {
      String sourceInfo = bindingParams.getInputStreamParams().get(i).getSourceInfo()
          .getSourceId();
      eventInfoMap.put(sourceInfo, i);
    }
  }

  public Event makeEvent(Map<String, Object> mapEvent, String sourceId) {
    return EventFactory.fromMap(mapEvent, getSourceInfo(getIndex(sourceId)), getSchemaInfo
        (getIndex(sourceId)));

  }

  public List<SourceInfo> getSourceInfo() {
    return bindingParams.getInputStreamParams().size() == 1 ? Collections.singletonList
        (getSourceInfo(0)) : Arrays.asList(getSourceInfo(0), getSourceInfo(1));
  }

  public List<SchemaInfo> getSchemaInfo() {
    return bindingParams.getInputStreamParams().size() == 1 ? Collections.singletonList
        (getSchemaInfo(0)) : Arrays.asList(getSchemaInfo(0), getSchemaInfo(1));
  }

  public SourceInfo getSourceInfo(Integer index) {
    return bindingParams.getInputStreamParams().get(index).getSourceInfo();
  }

  public SchemaInfo getSchemaInfo(Integer index) {
    return bindingParams.getInputStreamParams().get(index).getSchemaInfo();
  }

  private Integer getIndex(String sourceId) {
    return eventInfoMap.get(sourceId);
  }

  public X getRuntimeContext() {
    return runtimeContext;
  }

  public Boolean isSingletonEngine() {
    return singletonEngine;
  }

  protected abstract X makeRuntimeContext();

}

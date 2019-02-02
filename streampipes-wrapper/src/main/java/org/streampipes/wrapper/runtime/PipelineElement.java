/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.EventFactory;
import org.streampipes.model.runtime.SchemaInfo;
import org.streampipes.model.runtime.SourceInfo;
import org.streampipes.wrapper.params.binding.BindingParams;

import java.util.HashMap;
import java.util.Map;

public abstract class PipelineElement<B extends BindingParams<I>, I extends
        InvocableStreamPipesEntity> {

  private I graph;
  private B bindingParameters;
  private Map<String, Integer> eventInfoMap = new HashMap<>();

  public PipelineElement(B params) {
    this.bindingParameters = params;
    this.graph = params.getGraph();
    buildEventInfoMap();
  }

  private void buildEventInfoMap() {
    for(int i = 0; i < bindingParameters.getInputStreamParams().size(); i++) {
      String sourceInfo = bindingParameters.getInputStreamParams().get(i).getSourceInfo()
              .getSourceId();
      eventInfoMap.put(sourceInfo, i);
    }
  }

  public I getGraph() {
    return this.graph;
  }

  protected Event makeEvent(Map<String, Object> mapEvent, String sourceId) {
    return EventFactory.fromMap(mapEvent, getSourceInfo(getIndex(sourceId)), getSchemaInfo
            (getIndex(sourceId)));

  }

  private SourceInfo getSourceInfo(Integer index) {
    return bindingParameters.getInputStreamParams().get(index).getSourceInfo();
  }

  private SchemaInfo getSchemaInfo(Integer index) {
    return bindingParameters.getInputStreamParams().get(index).getSchemaInfo();
  }

  private Integer getIndex(String sourceId) {
    return eventInfoMap.get(sourceId);
  }


  public abstract void onEvent(Map<String, Object> inputEvent, String sourceInfo);

  public abstract void discard() throws SpRuntimeException;
}

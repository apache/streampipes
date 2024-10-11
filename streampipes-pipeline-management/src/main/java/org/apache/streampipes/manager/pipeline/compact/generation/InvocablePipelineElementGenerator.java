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

package org.apache.streampipes.manager.pipeline.compact.generation;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;

public class InvocablePipelineElementGenerator<T extends InvocableStreamPipesEntity> {

  public static final String ID_PREFIX = "jsplumb_";

  public void apply(T element,
                    CompactPipelineElement compatPipelineElement) {
    element.setDom(ID_PREFIX + compatPipelineElement.ref());
    element.setConnectedTo(compatPipelineElement.connectedTo().stream().map(c -> ID_PREFIX + c).toList());
    element.setStreamRequirements(new Cloner().streams(element.getStreamRequirements()));
  }

  protected PipelineElementTemplate makeTemplate(T element,
                                                 CompactPipelineElement compactPipelineElement) {
    var configs = compactPipelineElement.configuration();
    if (compactPipelineElement.configuration() == null) {
      configs = new ArrayList<>();
    }
    var template = new PipelineElementTemplate();
    template.setTemplateConfigs(configs);
    template.setBasePipelineElementAppId(element.getAppId());

    return template;
  }
}

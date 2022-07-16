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

package org.apache.streampipes.manager.template;

import org.apache.streampipes.model.template.PipelineElementTemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTemplateHandler<T> {

  protected T element;
  protected PipelineElementTemplate template;

  protected boolean overwriteNameAndDescription;

  public AbstractTemplateHandler(PipelineElementTemplate template,
                                 T element,
                                 boolean overwriteNameAndDescription) {
    this.template = template;
    this.element = element;
    this.overwriteNameAndDescription = overwriteNameAndDescription;
  }

  public T applyTemplateOnPipelineElement() {
    Map<String, Object> configs = new HashMap<>();
    template.getTemplateConfigs().forEach((key, value) -> configs.put(key, value.getValue()));
    PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(configs);
    visitStaticProperties(visitor);

    if (overwriteNameAndDescription) {
      applyNameAndDescription(template.getTemplateName(), template.getTemplateDescription());
    }

    return element;
  }

  protected abstract void visitStaticProperties(PipelineElementTemplateVisitor visitor);

  protected abstract void applyNameAndDescription(String name, String description);
}

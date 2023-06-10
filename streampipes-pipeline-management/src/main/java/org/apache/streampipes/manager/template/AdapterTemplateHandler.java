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

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.template.PipelineElementTemplate;

public class AdapterTemplateHandler extends AbstractTemplateHandler<AdapterDescription> {

  public AdapterTemplateHandler(PipelineElementTemplate template,
                                AdapterDescription adapterDescription,
                                boolean overwriteNameAndDescription) {
    super(template, adapterDescription, overwriteNameAndDescription);
  }

  @Override
  protected void visitStaticProperties(PipelineElementTemplateVisitor visitor) {
    element.getConfig().forEach(config -> config.accept(visitor));
  }

  @Override
  protected void applyNameAndDescription(String name, String description) {
    element.setName(name);
    element.setDescription(description);
  }
}

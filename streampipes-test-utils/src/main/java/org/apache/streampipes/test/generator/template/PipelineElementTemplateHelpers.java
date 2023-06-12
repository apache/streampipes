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
package org.apache.streampipes.test.generator.template;

import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;

import java.util.HashMap;
import java.util.Map;

public class PipelineElementTemplateHelpers {

  public static PipelineElementTemplate makePipelineElementTemplate() {
    Map<String, PipelineElementTemplateConfig> configs = new HashMap<>();
    configs.put("test-key", makeConfig(true, true, "test-string"));
    configs.put("test-key-2", makeConfig(true, false, 2));

    return new PipelineElementTemplate("name", "description", configs);
  }

  public static PipelineElementTemplateConfig makeConfig(boolean editable, boolean displayed, Object value) {
    return new PipelineElementTemplateConfig(editable, displayed, value);
  }
}

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
package org.apache.streampipes.storage.couchdb.serializer;

import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.test.generator.template.PipelineElementTemplateHelpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGsonSerializer {
  public static void assertions(PipelineElementTemplate template) {
    Assertions.assertEquals("name", template.getTemplateName());
    Assertions.assertEquals("description", template.getTemplateDescription());
    Assertions.assertEquals(2,
                            template.getTemplateConfigs().size());
    Assertions.assertEquals("test-string",
                            template.getTemplateConfigs().get("test-key").getValue());
    Assertions.assertTrue(template.getTemplateConfigs().get("test-key").isEditable());
    Assertions.assertTrue(template.getTemplateConfigs().get("test-key").isDisplayed());
    Assertions.assertTrue(template.getTemplateConfigs().get("test-key-2").isEditable());
    Assertions.assertFalse(template.getTemplateConfigs().get("test-key-2").isDisplayed());
  }

  @Test
  public void testPipelineElementTemplateSerialization() {
    PipelineElementTemplate template = PipelineElementTemplateHelpers.makePipelineElementTemplate();

    String json = GsonSerializer.getGsonWithIds().toJson(template);
    PipelineElementTemplate template2 = GsonSerializer.getGsonWithIds().fromJson(json, PipelineElementTemplate.class);
    assertions(template2);
    Assertions.assertEquals(2.0,
                            template2.getTemplateConfigs().get("test-key-2").getValue());
  }
}

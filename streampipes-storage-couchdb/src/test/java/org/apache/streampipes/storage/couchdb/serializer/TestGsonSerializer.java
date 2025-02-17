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
    Assertions.assertEquals(
        2,
        template.getTemplateConfigs()
                .size()
    );
    Assertions.assertEquals(
        "test-string",
        findValue(template, "test-key")
    );
  }

  private static Object findValue(PipelineElementTemplate template,
                           String key) {
    return template.getTemplateConfigs()
        .stream()
        .filter(t -> t.containsKey(key))
        .map(t -> t.get(key))
        .findFirst()
        .orElseThrow(() -> new AssertionError("test-key not found"));
  }

  @Test
  public void testPipelineElementTemplateSerialization() {
    PipelineElementTemplate template = PipelineElementTemplateHelpers.makePipelineElementTemplate();

    String json = GsonSerializer.getGsonBuilder()
                                .create()
                                .toJson(template);
    PipelineElementTemplate template2 = GsonSerializer
        .getGsonBuilder()
        .create()
        .fromJson(json, PipelineElementTemplate.class);
    assertions(template2);
    Assertions.assertEquals(
        2.0,
        findValue(template2, "test-key-2")
    );
  }
}

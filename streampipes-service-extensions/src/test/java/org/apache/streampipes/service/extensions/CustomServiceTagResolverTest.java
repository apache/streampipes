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
package org.apache.streampipes.service.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomServiceTagResolverTest {

  private StringEnvironmentVariable customServiceTags;
  private CustomServiceTagResolver resolver;

  @BeforeEach
  void setUp() {
    var env = mock(Environment.class);
    customServiceTags = mock(StringEnvironmentVariable.class);
    when(env.getCustomServiceTags()).thenReturn(customServiceTags);
    resolver = new CustomServiceTagResolver(env);
  }

  @Test
  void returnsCustomServiceTagsWhenTheyExist() {
    when(customServiceTags.exists()).thenReturn(true);
    when(customServiceTags.getValue()).thenReturn("tag1,tag2");

    var result = resolver.getCustomServiceTags();

    assertEquals(2, result.size());
    assertTrue(result.contains(SpServiceTag.create(SpServiceTagPrefix.CUSTOM, "tag1")));
    assertTrue(result.contains(SpServiceTag.create(SpServiceTagPrefix.CUSTOM, "tag2")));
  }

  @Test
  void returnsEmptySetWhenNoCustomServiceTagsExist() {
    when(customServiceTags.exists()).thenReturn(false);

    var result = resolver.getCustomServiceTags();

    assertTrue(result.isEmpty());
  }
}
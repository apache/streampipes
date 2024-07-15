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

import org.apache.streampipes.model.staticproperty.MappingPropertyNary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineElementTemplateVisitorTest {
  private static final String PROPERTY_NAME = "propertyName";

  private PipelineElementTemplateVisitor visitor;
  private Map<String, Object> configs;
  private MappingPropertyNary mappingPropertyNary;

  @BeforeEach
  void setUp() {
    configs = new HashMap<>();
    visitor = new PipelineElementTemplateVisitor(configs);
    mappingPropertyNary = mock(MappingPropertyNary.class);
    when(mappingPropertyNary.getInternalName()).thenReturn(PROPERTY_NAME);
  }

  @Test
  void visitMappingPropertyNary_SetsSelectedPropertiesWhenKeyExists() {
    List<String> expectedValues = List.of("value1", "value2");
    configs.put(PROPERTY_NAME, expectedValues);
    visitor.visit(mappingPropertyNary);

    verify(mappingPropertyNary).setSelectedProperties(expectedValues);
  }

  @Test
  void visitMappingPropertyNary_DoesNothingWhenKeyDoesNotExist() {
    visitor.visit(mappingPropertyNary);

    verify(mappingPropertyNary, never()).setSelectedProperties(anyList());
  }

  @Test
  void visitMappingPropertyNary_HandlesEmptySelectedPropertiesCorrectly() {
    List<String> expectedValues = List.of();
    configs.put(PROPERTY_NAME, expectedValues);
    visitor.visit(mappingPropertyNary);

    verify(mappingPropertyNary).setSelectedProperties(expectedValues);
  }
}

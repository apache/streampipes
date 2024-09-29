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
package org.apache.streampipes.sdk;

import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StaticPropertiesTest {

  private static final String TEST_PROPERTY_LABEL = "test-property-id";
  private static final Label defaultLabel = Labels.from("", TEST_PROPERTY_LABEL, "");

  @Test
  public void codeStaticProperty() {
    String codeTemplate = "// This is a test";
    var result = StaticProperties.codeStaticProperty(defaultLabel, CodeLanguage.Javascript, codeTemplate);

    Assertions.assertEquals(TEST_PROPERTY_LABEL, result.getLabel());
    Assertions.assertEquals(CodeLanguage.Javascript.name(), result.getLanguage());
    Assertions.assertEquals(codeTemplate, result.getCodeTemplate());
  }
}
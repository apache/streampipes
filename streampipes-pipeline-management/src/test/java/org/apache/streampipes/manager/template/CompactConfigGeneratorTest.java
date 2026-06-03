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

import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompactConfigGeneratorTest {

  private static final String PROPERTY_NAME = "propertyName";

  @Test
  void oneOfStaticPropertyStoresSelectedOptionName() {
    var property = new OneOfStaticProperty(PROPERTY_NAME, "", "");
    property.setOptions(List.of(
        new Option("option1"),
        new Option("option2", true)
    ));

    Map<String, Object> config = new CompactConfigGenerator(property).toTemplateValue();

    assertEquals("option2", config.get(PROPERTY_NAME));
    assertInstanceOf(String.class, config.get(PROPERTY_NAME));
  }

  @Test
  void oneOfStaticPropertyRoundTripsSelectedOption() {
    var property = new OneOfStaticProperty(PROPERTY_NAME, "", "");
    property.setOptions(List.of(
        new Option("option1"),
        new Option("option2", true)
    ));

    var config = new CompactConfigGenerator(property).toTemplateValue();
    var restoredProperty = new OneOfStaticProperty(PROPERTY_NAME, "", "");
    restoredProperty.setOptions(List.of(
        new Option("option1"),
        new Option("option2")
    ));

    new PipelineElementTemplateVisitor(List.of(config)).visit(restoredProperty);

    assertFalse(restoredProperty.getOptions().get(0).isSelected());
    assertTrue(restoredProperty.getOptions().get(1).isSelected());
  }

  @Test
  void optionalOneOfStaticPropertyWithoutSelectionOmitsConfig() {
    var property = new OneOfStaticProperty(PROPERTY_NAME, "", "");
    property.setOptional(true);
    property.setOptions(List.of(
        new Option("option1"),
        new Option("option2")
    ));

    Map<String, Object> config = new CompactConfigGenerator(property).toTemplateValue();

    assertTrue(config.isEmpty());
  }
}

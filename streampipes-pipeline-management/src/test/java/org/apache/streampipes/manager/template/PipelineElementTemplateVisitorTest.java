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

import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineElementTemplateVisitorTest {
  private static final String PROPERTY_NAME = "propertyName";

  private PipelineElementTemplateVisitor visitor;
  private List<Map<String, Object>> configs;
  private MappingPropertyNary mappingPropertyNary;

  @BeforeEach
  void setUp() {
    configs = new ArrayList<>();
    visitor = new PipelineElementTemplateVisitor(configs);
    mappingPropertyNary = mock(MappingPropertyNary.class);
    when(mappingPropertyNary.getInternalName()).thenReturn(PROPERTY_NAME);
  }

  @Test
  void visitMappingPropertyNary_SetsSelectedPropertiesWhenKeyExists() {
    List<String> expectedValues = List.of("value1", "value2");
    configs.add(Map.of(PROPERTY_NAME, expectedValues));
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
    configs.add(Map.of(PROPERTY_NAME, expectedValues));
    visitor.visit(mappingPropertyNary);

    verify(mappingPropertyNary).setSelectedProperties(expectedValues);
  }

  @Test
  void anyStaticPropertyTest() {
    configs.add(Map.of(PROPERTY_NAME, List.of("option1", "option2")));
    var anyStaticProperty = new AnyStaticProperty(PROPERTY_NAME, "", "");
    anyStaticProperty.setOptions(List.of(
            new Option("option1"),
            new Option("option2")
        )
    );
    visitor.visit(anyStaticProperty);
    assertEquals(2, anyStaticProperty.getOptions().size());
    assertTrue(anyStaticProperty.getOptions().get(0).isSelected());
    assertTrue(anyStaticProperty.getOptions().get(1).isSelected());
  }

  @Test
  void colorPickerStaticPropertyTest() {
    configs.add(Map.of(PROPERTY_NAME, "red"));
    var colorPickerStaticProperty = new ColorPickerStaticProperty(PROPERTY_NAME, "", "");

    visitor.visit(colorPickerStaticProperty);
    assertEquals("red", colorPickerStaticProperty.getSelectedColor());
  }

  @Test
  void simpleCollectionStaticPropertyTest() {
    var freeTextId = "free_text";
    configs.add(Map.of(PROPERTY_NAME, List.of(
        Map.of(freeTextId, "A"),
        Map.of(freeTextId, "B"),
        Map.of(freeTextId, "C")))
    );
    var collectionProperty = new CollectionStaticProperty(
        PROPERTY_NAME,
        "",
        "",
        new FreeTextStaticProperty(freeTextId, "", "")
    );

    visitor.visit(collectionProperty);
    assertEquals(3, collectionProperty.getMembers().size());
    assertEquals(FreeTextStaticProperty.class, collectionProperty.getMembers().get(0).getClass());
    assertEquals("A", ((FreeTextStaticProperty) collectionProperty.getMembers().get(0)).getValue());
  }

  @Test
  void secretStaticPropertyTest() {
    configs.add(Map.of(PROPERTY_NAME, "secret", "encrypted", true));
    var secretStaticProperty = new SecretStaticProperty(PROPERTY_NAME, "", "");

    visitor.visit(secretStaticProperty);
    assertEquals("secret", secretStaticProperty.getValue());
    assertEquals(true, secretStaticProperty.getEncrypted());
  }

  @Test
  void alternativesStaticPropertyTest() {
    configs.add(Map.of(
        PROPERTY_NAME, "alternatives1",
        "free_text", "abc")
    );
    var staticProperty = new StaticPropertyAlternatives(PROPERTY_NAME, "", "");
    staticProperty.setAlternatives(
        List.of(
            Alternatives.from(
                Labels.withId("alternatives1"),
                new FreeTextStaticProperty("free_text", "", "")
            ),
            Alternatives.from(
                Labels.withId("alternatives2"),
                new OneOfStaticProperty("one_of", "", "")
            )
        )
    );

    visitor.visit(staticProperty);
    assertTrue(staticProperty.getAlternatives().get(0).getSelected());
    assertFalse(staticProperty.getAlternatives().get(1).getSelected());
    assertEquals(
        "abc",
        ((FreeTextStaticProperty) staticProperty.getAlternatives().get(0).getStaticProperty()).getValue()
    );
  }

  @Test
  void alternativesStaticPropertyWithGroupTest() {
    configs.add(Map.of(
        PROPERTY_NAME, "alternatives2",
        "free_text", "abc",
        "free_text_number", 1000)
    );
    var staticProperty = new StaticPropertyAlternatives(PROPERTY_NAME, "", "");
    staticProperty.setAlternatives(
        List.of(
            Alternatives.from(
                Labels.withId("alternatives1"),
                new OneOfStaticProperty("one_of", "", "")
            ),
            Alternatives.from(
                Labels.withId("alternatives2"),
                new StaticPropertyGroup("group", "", "", List.of(
                    new FreeTextStaticProperty("free_text", "", ""),
                    new FreeTextStaticProperty("free_text_number", "", ""))
                )
            )
        )
    );

    visitor.visit(staticProperty);
    assertTrue(staticProperty.getAlternatives().get(1).getSelected());
    assertFalse(staticProperty.getAlternatives().get(0).getSelected());

    var group = (StaticPropertyGroup) staticProperty.getAlternatives().get(1).getStaticProperty();
    var freeText = ((FreeTextStaticProperty) group.getStaticProperties().get(0)).getValue();
    var freeTextNumber = ((FreeTextStaticProperty) group.getStaticProperties().get(1)).getValue();
    assertEquals("abc", freeText);
    assertEquals("1000", freeTextNumber);
  }

  @Test
  void treeInputStaticPropertyTest() {
    configs.add(Map.of(PROPERTY_NAME, "secret", "encrypted", true));
    var secretStaticProperty = new SecretStaticProperty(PROPERTY_NAME, "", "");

    visitor.visit(secretStaticProperty);
    assertEquals("secret", secretStaticProperty.getValue());
    assertEquals(true, secretStaticProperty.getEncrypted());
  }
}

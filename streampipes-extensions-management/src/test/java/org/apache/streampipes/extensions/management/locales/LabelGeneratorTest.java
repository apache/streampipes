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
package org.apache.streampipes.extensions.management.locales;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LabelGeneratorTest {

  private static final String TEST_APP_ID = "test-app-id";

  @Test
  public void getElementDescriptionThrowException() {
    var labelGenerator = getLabelGeneratorWithoutLocalesFile();
    assertThrows(IOException.class, labelGenerator::getElementDescription);
  }

  @Test
  public void getElementDescriptionReturnsCorrectDescription() throws IOException {
    var expectedDescription = "test-description";
    var properties = getProperties(LabelGenerator.DESCRIPTION, expectedDescription);

    var labelGenerator = getLabelGeneratorWithProperties(properties);

    Assertions.assertEquals(expectedDescription, labelGenerator.getElementDescription());
  }

  @Test
  public void getElementTitleThrowExceptiojn() {
    var labelGenerator = getLabelGeneratorWithoutLocalesFile();
    assertThrows(IOException.class, labelGenerator::getElementTitle);
  }

  @Test
  public void getElementTitleReturnsCorrectDescription() throws IOException {
    var expectedTitle = "test-title";
    var properties = getProperties(LabelGenerator.TITLE, expectedTitle);

    var labelGenerator = getLabelGeneratorWithProperties(properties);

    Assertions.assertEquals(expectedTitle, labelGenerator.getElementTitle());
  }

  private LabelGenerator getLabelGeneratorWithoutLocalesFile() {
    var mockDescription = Mockito.mock(NamedStreamPipesEntity.class);
    when(mockDescription.getAppId()).thenReturn(TEST_APP_ID);

    var labelGenerator = new LabelGenerator(mockDescription);
    var labelGeneratorMock = spy(labelGenerator);
    return labelGeneratorMock;
  }

  private LabelGenerator getLabelGeneratorWithProperties(Properties properties) throws IOException {
    var mockDescription = Mockito.mock(NamedStreamPipesEntity.class);
    when(mockDescription.getAppId()).thenReturn(TEST_APP_ID);

    var labelGenerator = new LabelGenerator(mockDescription);
    var labelGeneratorMock = spy(labelGenerator);
    doReturn(properties).when(labelGeneratorMock).loadResourceAndMakeProperties();
    return labelGeneratorMock;
  }

  private Properties getProperties(String key, String value) {
    var result = new Properties();
    result.setProperty(TEST_APP_ID + LabelGenerator.DELIMITER + key, value);

    return result;
  }
}

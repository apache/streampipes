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
package org.apache.streampipes.sdk.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class LabelsTest {

  private static final String ID = "testId";
  private static final String LABEL = "TestLabel";
  private static final String INTERNAL_ID = "internalId";
  private static final String DESCRIPTION = "TestDescription";

  private enum TestEnum {
    TEST
  }

  @Test
  public void from_CreatesLabelWithAllFields() {
    var result = Labels.from(ID, LABEL, DESCRIPTION);

    assertEquals(ID, result.getInternalId());
    assertEquals(LABEL, result.getLabel());
    assertEquals(DESCRIPTION, result.getDescription());
  }

  @Test
  public void from_HandlesEmptyLabelAndDescription() {
    var result = Labels.from(ID, "", "");

    assertEquals(ID, result.getInternalId());
    assertEquals("", result.getLabel());
    assertEquals("", result.getDescription());
  }

  @Test
  public void from_HandlesNullLabelAndDescription() {
    var result = Labels.from(ID, null, null);

    assertEquals("testId", result.getInternalId());
    assertNull(result.getLabel());
    assertNull(result.getDescription());
  }

  @Test
  public void withId_CreatesLabelWithInternalIdOnly() {
    var result = Labels.withId(INTERNAL_ID);

    assertEquals(INTERNAL_ID, result.getInternalId());
    assertEquals("", result.getLabel());
    assertEquals("", result.getDescription());
  }

  @Test
  public void withId_HandlesEmptyStringInternalId() {
    var result = Labels.withId("");

    assertEquals("", result.getInternalId());
    assertEquals("", result.getLabel());
    assertEquals("", result.getDescription());
  }

  @Test
  public void withId_CreatesLabelFromEnumWithCorrectInternalId() {
    var result = Labels.withId(TestEnum.TEST);
    assertEquals(TestEnum.TEST.name(), result.getInternalId());
  }

  @Test
  public void empty_CreatesLabelWithEmptyFields() {
    var result = Labels.empty();

    assertEquals("", result.getInternalId());
    assertEquals("", result.getLabel());
    assertEquals("", result.getDescription());
  }

}
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
package org.apache.streampipes.connect.shared.preprocessing.transform.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class DeleteTransformationRuleTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void transformSimple() {
    var event = new HashMap<String, Object>();
    event.put("key", "value");

    var deleteRule = new DeleteTransformationRule(List.of("key"));

    var result = deleteRule.apply(event);

    assertEquals(0, result.keySet().size());
  }

  @Test
  public void transformNested() throws JsonProcessingException {
    var jsonString = """
            {
                "parent": {
                    "child": "value",
                    "child2": "value2"
                },
                "keepProperty": "test"
            }
            """;

    var event = getEvent(jsonString);

    var deleteRule = new DeleteTransformationRule(List.of("parent", "child"));

    var result = deleteRule.apply(event);

    assertEquals(2, result.keySet().size());
    assertEquals(1, ((Map) result.get("parent")).size());
  }

  @Test
  public void testRemoveParent() throws JsonProcessingException {
    var jsonString = """
            {
                "parent": {
                    "child": "value",
                    "child2": "value2"
                },
                "keepProperty": "test"
            }
            """;

    var event = getEvent(jsonString);

    var deleteRule = new DeleteTransformationRule(List.of("parent"));

    var result = deleteRule.apply(event);

    assertEquals(1, result.keySet().size());
    assertEquals("test", result.get("keepProperty"));
  }

  @Test
  // verifying that mathod applyTransformation method works when passed a null event.
  public void applyTransformationWithNullParameter() {
    new DeleteTransformationRule(List.of()).applyTransformation(null, List.of());
  }

  @Test
  public void deleteNestedChildWithParentProperty() throws JsonProcessingException {

    var jsonString = """
            {
                "parent": {
                    "child": "value"
                },
                "keepProperty": "test"
            }
            """;

    var event = getEvent(jsonString);

    var deleteRule = new DeleteTransformationRule(Arrays.asList("parent", "child"));

    var result = deleteRule.apply(event);

    assertEquals(2, result.keySet().size());
    assertEquals("test", result.get("keepProperty"));
  }

  private Map<String, Object> getEvent(String eventJson) throws JsonProcessingException {
    return objectMapper.readValue(eventJson, HashMap.class);
  }

}

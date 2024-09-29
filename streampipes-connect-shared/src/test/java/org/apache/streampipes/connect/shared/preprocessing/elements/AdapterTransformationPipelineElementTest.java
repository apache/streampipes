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
package org.apache.streampipes.connect.shared.preprocessing.elements;

import static org.mockito.Mockito.when;

import org.apache.streampipes.connect.shared.preprocessing.generator.StatelessTransformationRuleGeneratorVisitor;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AdapterTransformationPipelineElementTest {

  @Test
  public void testTransformationPipelineElement() {
    var renameRuleDescription = new RenameRuleDescription();
    var spy = Mockito.spy(renameRuleDescription);
    when(spy.getOldRuntimeKey()).thenReturn("temperature");
    when(spy.getNewRuntimeKey()).thenReturn("temp");

    var addTimestampRule = new AddTimestampRuleDescription("timestamp");

    var rules = List.of(spy, addTimestampRule);

    var pipelineElement = new AdapterTransformationPipelineElement(rules,
            new StatelessTransformationRuleGeneratorVisitor());
    var event = new HashMap<String, Object>();
    event.put("temperature", 1);

    var result = pipelineElement.process(event);

    Assertions.assertEquals(2, result.size());
    Assertions.assertTrue(result.containsKey("temp"));
    Assertions.assertFalse(result.containsKey("temperature"));
    Assertions.assertTrue(result.containsKey("timestamp"));
  }
}

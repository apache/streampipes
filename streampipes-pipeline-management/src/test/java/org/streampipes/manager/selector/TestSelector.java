/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.manager.selector;

import static org.junit.Assert.assertEquals;
import static org.streampipes.manager.selector.TestSelectorUtils.makeSchema;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.model.schema.EventProperty;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class TestSelector {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {Arrays.asList("s0::testDimension"), 1},
            {Arrays.asList("s0::location", "s0::location::latitude"), 1},
            {Arrays.asList("s0::testDimension", "s0::testMeasurement"), 2}

    });
  }

  @Parameterized.Parameter
  public List<String> fieldSelectors;

  @Parameterized.Parameter(1)
  public int expectedPropertyCount;

  @Test
  public void test() {
    List<EventProperty> newProperties = new PropertySelector(makeSchema())
            .createPropertyList(fieldSelectors);

    assertEquals(expectedPropertyCount, newProperties.size());

  }
}

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
package org.streampipes.model.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

public class TestListField {

  @Test
  public void testListParser() {
    Event event = RuntimeTestUtils.makeSimpleEvent(RuntimeTestUtils.listMap(), RuntimeTestUtils.getSourceInfo());

    List<Integer> integerList = event.getFieldByRuntimeName("list").getAsList().parseAsSimpleType
            (Integer.class);

    assertEquals(3, integerList.size());
    assertEquals(Integer.valueOf(1), integerList.get(0));
    assertEquals(Integer.valueOf(2), integerList.get(1));
    assertEquals(Integer.valueOf(3), integerList.get(2));
  }

}

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

import org.junit.Test;

import java.util.List;


public class TestSelectorGenerator {

  @Test
  public void test() {
    List<String> propertySelectors = new PropertySelectorGenerator(TestSelectorUtils.makeSchema()
            , false)
            .generateSelectors();

    assertEquals(10, propertySelectors.size());
  }

  @Test
  public void testTwoSchemas() {
    List<String> propertySelectors = new PropertySelectorGenerator(TestSelectorUtils.makeSchema()
            , TestSelectorUtils.makeSchema(), false)
            .generateSelectors();

    assertEquals(20, propertySelectors.size());
  }

}

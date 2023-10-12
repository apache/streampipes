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

package org.apache.streampipes.connect.adapters.generic.elements;

import org.apache.streampipes.connect.shared.preprocessing.transform.stream.DuplicateFilterPipelineElement;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DuplicateFilterTest {

  @Test
  public void duplicateSimple() {
    DuplicateFilterPipelineElement duplicateFilter = new DuplicateFilterPipelineElement("0");
    List<Map<String, Object>> events = generateEvents();

    assertNotNull(duplicateFilter.apply(events.get(0)));
    assertNull(duplicateFilter.apply(events.get(0)));

  }

  @Test
  public void duplicateComplex() {
    DuplicateFilterPipelineElement duplicateFilter = new DuplicateFilterPipelineElement("0");
    List<Map<String, Object>> events = generateEvents();

    assertNotNull(duplicateFilter.apply(events.get(0)));
    assertNotNull(duplicateFilter.apply(events.get(1)));
    assertNotNull(duplicateFilter.apply(events.get(2)));
    assertNotNull(duplicateFilter.apply(events.get(3)));
    assertNotNull(duplicateFilter.apply(events.get(4)));
    assertNotNull(duplicateFilter.apply(events.get(5)));
    assertNotNull(duplicateFilter.apply(events.get(6)));
    assertNotNull(duplicateFilter.apply(events.get(7)));
    assertNull(duplicateFilter.apply(events.get(0)));

  }


    /* TODO: To stir up the test, adjust the static parameters in the class
            CLEAN_UP_INTERVAL_MILLI_SEC = 1000 * 5; //5 Sec
            LIFE_TIME_EVENT_DUPLICATE = 1000 * 1; //1 Sec
     */
  /*  @Test
    public void CleanUp1() throws InterruptedException {
        DuplicateFilter duplicateFilter = new DuplicateFilter();
        List<Map> events = generateEvents();

        assertNotNull(duplicateFilter.process(events.get(0)));
        assertNull(duplicateFilter.process(events.get(0)));
        Thread.sleep(7000);
        assertNotNull(duplicateFilter.process(events.get(0)));


    }
*/
    /* TODO: To stir up the test, adjust the static parameters in the class
         CLEAN_UP_INTERVAL_MILLI_SEC = 1000 * 5; //5 Sec
         LIFE_TIME_EVENT_DUPLICATE = 1000 * 1; //1 Sec
    */
  /*  @Test
    public void CleanUp2() throws InterruptedException {
        DuplicateFilter duplicateFilter = new DuplicateFilter();
        List<Map> events = generateEvents();

        assertNotNull(duplicateFilter.process(events.get(0)));
        assertNull(duplicateFilter.process(events.get(0)));
        assertNotNull(duplicateFilter.process(events.get(1)));
        assertNotNull(duplicateFilter.process(events.get(4)));
        assertNotNull(duplicateFilter.process(events.get(5)));
        Thread.sleep(7000);
        assertNotNull(duplicateFilter.process(events.get(0)));
        assertNotNull(duplicateFilter.process(events.get(4)));
        assertNotNull(duplicateFilter.process(events.get(5)));
        assertNull(duplicateFilter.process(events.get(5)));
        assertNull(duplicateFilter.process(events.get(0)));
        Thread.sleep(7000);
        assertNotNull(duplicateFilter.process(events.get(0)));
        assertNotNull(duplicateFilter.process(events.get(4)));
        assertNotNull(duplicateFilter.process(events.get(5)));

    }
*/


  private List<Map<String, Object>> generateEvents() {
    List<Map<String, Object>> list = new LinkedList<>();

    list.add(makeMap("Test", 123));
    list.add(makeMap("Test", 1234));
    list.add(makeMap("Test", "Test"));
    list.add(makeMap("Name", "Piet"));
    list.add(makeMap("Test", "Test12"));
    list.add(makeMap("Age", "Smith"));
    list.add(makeMap("Street", "Heidenstreet"));
    list.add(makeMap("Country", "DE"));
    list.add(makeMap("City", "Blank City"));

    return list;
  }

  private Map<String, Object> makeMap(String key, Object value) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(key, value);
    return map;
  }
}

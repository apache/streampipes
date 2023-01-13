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

package org.apache.streampipes.extensions.management.connect.adapter.format.xml;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class XmlMapConverterTest {

  private String eventKey = "key";

  @Test
  public void convertLongValue() {
    var event = new HashMap<String, Object>();
    event.put(eventKey, "1667904471000");

    XmlMapConverter xmlMapConverter = new XmlMapConverter(event);

    var result = xmlMapConverter.convert();
    assertEquals(1, result.size());
    assertEquals(1667904471000L, result.get(eventKey));
  }

  @Test
  public void convertBooleanValue() {
    var event = new HashMap<String, Object>();
    event.put(eventKey, "false");

    XmlMapConverter xmlMapConverter = new XmlMapConverter(event);

    var result = xmlMapConverter.convert();
    assertEquals(1, result.size());
    assertEquals(false, result.get(eventKey));
  }


}
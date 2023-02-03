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

package org.apache.streampipes.integration.svcdiscovery;

import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsulSpConfigTest extends AbstractConsulTest {

  private SpConfig spConfig;
  @Before
  public void before() {
    var env = mockEnvironment();
    this.spConfig = SpServiceDiscovery
        .getSpConfig("test-service-group", env);
  }

  @Test
  public void testKvString() {

    spConfig.register("str", "abc", "desc");
    var value = spConfig.getString("str");
    assertEquals("abc", value);
  }

  @Test
  public void testKvInteger() {
    spConfig.register("int", 2, "desc");
    var value = spConfig.getInteger("int");
    assertEquals(2, value);
  }

  @Test
  public void testKvConfigItem() {
    var configItem = makeConfigItem("config", "value");
    spConfig.register(configItem);
    var value = spConfig.getConfigItem("config");
    assertEquals(configItem, value);
  }

  @Test
  public void testUpdateValue() {
    spConfig.register("stru", "abc", "desc");
    var value = spConfig.getString("stru");
    assertEquals("abc", value);
    spConfig.setString("stru", "abc2");
    var result = spConfig.getString("stru");
    assertEquals("abc2", result);
  }
}

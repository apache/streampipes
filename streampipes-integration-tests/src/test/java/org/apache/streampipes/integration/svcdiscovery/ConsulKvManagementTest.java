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
import org.apache.streampipes.svcdiscovery.api.ISpKvManagement;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConsulKvManagementTest extends AbstractConsulTest {

  private ISpKvManagement kvManagement;

  @Before
  public void before() {
    var environment = mockEnvironment();
    this.kvManagement = SpServiceDiscovery
        .getKeyValueStore(environment);
  }

  @Test
  public void testKv() {
    var test1Key = "a/test";
    var test2Key = "a/test2";
    var configItem1 = makeConfigItem(test1Key, "abc");
    var configItem2 = makeConfigItem(test2Key, "def");
    kvManagement.updateConfig(test1Key, serialize(configItem1), false);
    kvManagement.updateConfig(test2Key, serialize(configItem2), false);

    Map<String, String> result = kvManagement.getKeyValue("a");

    assertEquals(2, result.size());
    assertEquals(serialize(configItem1), result.get(test1Key));
    assertEquals(serialize(configItem2), result.get(test2Key));
  }

  private String serialize(ConfigItem item) {
    return new Gson().toJson(item);
  }
}

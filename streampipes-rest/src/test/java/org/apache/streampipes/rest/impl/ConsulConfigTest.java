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

package org.apache.streampipes.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.apache.streampipes.config.model.ConfigItem;
import org.apache.streampipes.container.util.ConsulUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConsulUtil.class })
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class ConsulConfigTest {

    @Test
    public void getConfigForService() {
        Map<String, String> keyValues = new HashMap<>();

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("description", "description value");
        valueObject.addProperty("value", "5984");
        valueObject.addProperty("valueType", "xs:integer");
        valueObject.addProperty("configurationScope", "CONTAINER_STARTUP_CONFIG");
        valueObject.addProperty("isPassword", "false");

        keyValues.put("key_01", valueObject.toString());

        PowerMockito.mockStatic(ConsulUtil.class);
        Mockito.when(ConsulUtil.getKeyValue(anyString()))
                .thenReturn(keyValues);

        ConsulConfig consulConfig = new ConsulConfig();

        List<ConfigItem> result = consulConfig.getConfigForService("service_id", "tag");

        assertNotNull(result);
        assertEquals(1, result.size());

        ConfigItem configItem = result.get(0);

        assertEquals("description value", configItem.getDescription());
        assertEquals("5984", configItem.getValue());
        assertEquals("xs:integer", configItem.getValueType());
        assertEquals(false, configItem.isPassword());
        assertEquals("key_01", configItem.getKey());
    }
}
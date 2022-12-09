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

package org.apache.streampipes.connect.adapters.sensemap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SensorNamesTest {

  @Test
  public void getKeyTest() {

    assertEquals(SensorNames.KEY_HUMIDITY, SensorNames.getKey(SensorNames.GERMAN_HUMIDITY));
    assertEquals(SensorNames.KEY_TEMPERATURE, SensorNames.getKey(SensorNames.GERMAN_TEMPERATURE));
    assertEquals(SensorNames.KEY_PRESSURE, SensorNames.getKey(SensorNames.GERMAN_PRESSURE));
    assertEquals(SensorNames.KEY_ILLUMINANCE, SensorNames.getKey(SensorNames.GERMAN_ILLUMINANCE));
    assertEquals(SensorNames.KEY_UV_INTENSITY, SensorNames.getKey(SensorNames.GERMAN_UV_INTENSITY));
    assertEquals(SensorNames.KEY_PM10, SensorNames.getKey(SensorNames.GERMAN_PM10));
    assertEquals(SensorNames.KEY_PM2_5, SensorNames.getKey(SensorNames.GERMAN_PM2_5));
    assertEquals(SensorNames.KEY_PM2_5, SensorNames.getKey(SensorNames.GERMAN_PM2_5 + "suffix"));
    assertEquals(SensorNames.KEY_NOT_FOUND, SensorNames.getKey(""));

  }

  @Test
  public void getKeyFromLabelTest() {
    assertEquals(SensorNames.KEY_HUMIDITY, SensorNames.getKeyFromLabel(SensorNames.LABEL_HUMIDITY));
    assertEquals(SensorNames.KEY_TEMPERATURE, SensorNames.getKeyFromLabel(SensorNames.LABEL_TEMPERATURE));
    assertEquals(SensorNames.KEY_PRESSURE, SensorNames.getKeyFromLabel(SensorNames.LABEL_PRESSURE));
    assertEquals(SensorNames.KEY_ILLUMINANCE, SensorNames.getKeyFromLabel(SensorNames.LABEL_ILLUMINANCE));
    assertEquals(SensorNames.KEY_UV_INTENSITY, SensorNames.getKeyFromLabel(SensorNames.LABEL_UV_INTENSITY));
    assertEquals(SensorNames.KEY_PM10, SensorNames.getKeyFromLabel(SensorNames.LABEL_PM10));
    assertEquals(SensorNames.KEY_PM2_5, SensorNames.getKeyFromLabel(SensorNames.LABEL_PM2_5));
    assertEquals(SensorNames.KEY_NOT_FOUND, SensorNames.getKeyFromLabel(""));

  }


}
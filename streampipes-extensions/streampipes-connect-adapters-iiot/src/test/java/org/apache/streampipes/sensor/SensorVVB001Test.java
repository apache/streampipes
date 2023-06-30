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

package org.apache.streampipes.sensor;

import org.apache.streampipes.connect.iiot.adapters.iolink.sensor.SensorVVB001;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SensorVVB001Test {

  private SensorVVB001 sensor = new SensorVVB001();

  private String sampleInput = "0017FC000042FF000012FF00015CFF000025FF03";

  @Test
  public void checkEventSize() {
    var result = sensor.parseEvent(sampleInput);
    assertEquals(8, result.keySet().size());
  }

  @Test
  public void parseEvent() {
    var result = sensor.parseEvent(sampleInput);

    assertEquals(0.0023, (double) result.get(SensorVVB001.V_RMS_NAME), 0.001);
    assertEquals(6.600, (double) result.get(SensorVVB001.A_PEAK_NAME), 0.001);
    assertEquals(1.8, (double) result.get(SensorVVB001.A_RMS_NAME), 0.001);
    assertEquals(34.8, (double) result.get(SensorVVB001.TEMPERATURE_NAME), 0.001);
    assertEquals(3.7, (double) result.get(SensorVVB001.CREST_NAME), 0.001);
    assertEquals(0, result.get(SensorVVB001.STATUS_NAME));
    assertEquals(true, result.get(SensorVVB001.OUT_1_NAME));
    assertEquals(true, result.get(SensorVVB001.OUT_2_NAME));
  }
}
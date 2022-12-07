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

//public class OpenSenseMapAdapterTest {
//
//  private int senseMapMockPort = 4082;
//
//  @Rule
//  public WireMockRule wireMockRule = new WireMockRule(senseMapMockPort);
//
//  @Test
//  public void pullData() {
//
//    stubFor(get(urlEqualTo("/"))
//            .willReturn(aResponse()
//                    .withStatus(200)
//                    .withBody(getExampleStringEvent())));
//
//    OpenSenseMapAdapter adapter = new OpenSenseMapAdapter();
//    adapter.setUrl("http://localhost:" + senseMapMockPort);
//    adapter.setSelectedSensors(Arrays.asList(SensorNames.KEY_TEMPERATURE,
//    SensorNames.KEY_HUMIDITY, SensorNames.KEY_PRESSURE));
//
//    List<Map<String, Object>> result = adapter.getEvents();
//
//    assertNotNull(result);
//    assertEquals(1, result.size());
//
//    Map<String, Object> event = result.get(0);
//
//    assertEquals(event.keySet().size(), 9);
//
//    assertEquals(4.1, event.get(SensorNames.KEY_TEMPERATURE));
//    assertEquals(1536022861000L, event.get(SensorNames.KEY_TIMESTAMP));
//    assertEquals("CALIMERO", event.get(SensorNames.KEY_NAME));
//    assertEquals(98.0, event.get(SensorNames.KEY_HUMIDITY));
//    assertEquals("custom", event.get(SensorNames.KEY_MODEL));
//    assertEquals(100204.0, event.get(SensorNames.KEY_PRESSURE));
//    assertEquals("5386026e5f08822009b8b60d", event.get(SensorNames.KEY_ID));
//    assertEquals(10.59951787814498, event.get(SensorNames.KEY_LONGITUDE));
//    assertEquals(49.57165903690524, event.get(SensorNames.KEY_LATITUDE));
//
////        assertEquals("", event.get(SensorNames.KEY_TIMESTAMP));
//
//    // Mock get data from endpoint
//  }
//
//  private String getExampleStringEvent() {
//    return "[{\n"
//            + "\"_id\": \"5386026e5f08822009b8b60d\",\n"
//            + "\"name\": \"CALIMERO\",\n"
//            + "\"updatedAt\": \"2018-09-04T01:01:01.000Z\","
//            + "\"sensors\": [\n"
//            + "{\n"
//            + "\"__v\": 0,\n"
//            + "\"_id\": \"5386026e5f08822009b8b610\",\n"
//            + "\"boxes_id\": \"5386026e5f08822009b8b60d\",\n"
//            + "\"lastMeasurement\": {\n"
//            + "\"createdAt\": \"2015-02-23T22:38:53.854Z\",\n"
//            + "\"value\": \"100204\"\n"
//            + "},\n"
//            + "\"sensorType\": \"BMP085\",\n"
//            + "\"title\": \"Luftdruck\",\n"
//            + "\"unit\": \"Pa\"\n"
//            + "},\n"
//            + "{\n"
//            + "\"__v\": 0,\n"
//            + "\"_id\": \"5386026e5f08822009b8b611\",\n"
//            + "\"boxes_id\": \"5386026e5f08822009b8b60d\",\n"
//            + "\"lastMeasurement\": {\n"
//            + "\"createdAt\": \"2015-02-23T22:35:53.904Z\",\n"
//            + "\"value\": \"98\"\n"
//            + "},\n"
//            + "\"sensorType\": \"DHT11\",\n"
//            + "\"title\": \"rel. Luftfeuchte\",\n"
//            + "\"unit\": \"%\"\n"
//            + "},\n"
//            + "{\n"
//            + "\"__v\": 0,\n"
//            + "\"_id\": \"5386026e5f08822009b8b612\",\n"
//            + "\"boxes_id\": \"5386026e5f08822009b8b60d\",\n"
//            + "\"lastMeasurement\": {\n"
//            + "\"createdAt\": \"2015-02-23T22:36:18.843Z\",\n"
//            + "\"value\": \"4.1\"\n"
//            + "},\n"
//            + "\"sensorType\": \"BMP085\",\n"
//            + "\"title\": \"Temperatur\",\n"
//            + "\"unit\": \"°C\"\n"
//            + "}\n"
//            + "],\n"
//            + "\"exposure\": \"unknown\",\n"
//            + "\"createdAt\": \"2014-05-28T15:36:14.000Z\",\n"
//            + "\"model\": \"custom\",\n"
//            + "\"currentLocation\": {\n"
//            + "\"coordinates\": [\n"
//            + "10.59951787814498,\n"
//            + "49.57165903690524\n"
//            + "],\n"
//            + "\"type\": \"Point\",\n"
//            + "\"timestamp\": \"2014-05-28T15:36:14.000Z\"\n"
//            + "},\n"
//            + "\"loc\": [\n"
//            + "{\n"
//            + "\"geometry\": {\n"
//            + "\"coordinates\": [\n"
//            + "10.59951787814498,\n"
//            + "49.57165903690524\n"
//            + "],\n"
//            + "\"type\": \"Point\",\n"
//            + "\"timestamp\": \"2014-05-28T15:36:14.000Z\"\n"
//            + "},\n"
//            + "\"type\": \"Feature\"\n"
//            + "}\n"
//            + "]\n"
//            + "}]";
//  }
//}
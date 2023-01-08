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

package org.apache.streampipes.extensions.management.connect.adapter.format.json.geojson;

import org.apache.streampipes.extensions.management.connect.adapter.format.geojson.GeoJsonParser;
import org.apache.streampipes.model.schema.EventSchema;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


@SuppressWarnings("checkstyle:OperatorWrap")
public class GeoJsonTest {

  @Test
  public void getSchema1() {
    GeoJsonParser parser = new GeoJsonParser();

    byte[] event = getOneEventExampleMultiPolygon().getBytes(StandardCharsets.UTF_8);

    EventSchema eventSchema = parser.getEventSchema(Collections.singletonList(event));

    assertEquals(11, eventSchema.getEventProperties().size());

  }

  @Test
  public void getSchema2() {
    GeoJsonParser parser = new GeoJsonParser();

    byte[] event = getOneEventExample().getBytes(StandardCharsets.UTF_8);

    EventSchema eventSchema = parser.getEventSchema(Collections.singletonList(event));

    assertEquals(12, eventSchema.getEventProperties().size());

  }


  @Test
  public void parseOneEvent() {

    String jo = getFullExampleWithOneElement();

    GeoJsonParser parser = new GeoJsonParser();


    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

    assertEquals(parsedEvent.size(), 1);
    String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

    assertEquals(parsedStringEvent, "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":" +
                                    "[6.946535,51.437344]},\"properties\":{\"measurementOrCalculationTime\":"
                                    + "\"20180717121027\",\"publicationTime\""
                                    +
                                    ":\"20161026212501\",\"lorryFlowRate\":\"5\",\"lorryAverageVehicleSpeed\":"
                                    + "\"81.0\",\"anyVehicleAverageVehicleSpeed\""
                                    +
                                    ":\"81.0\",\"carAverageVehicleSpeed\":\"81.0\",\"anyVehicleFlowRate\":"
                                    + "\"15\",\"anyVehiclePercentageLongVehicle\""
                                    +
                                    ":\"33.0\",\"carFlowRate\":\"10\",\"id\":\"fs.MQ_A40-10E_HFB_NO_1\"}}");
  }


  @SuppressWarnings("checkstyle:OperatorWrap")
  @Test
  public void parseThreeEvents() {

    String jo = getFullExampleWithThreeElements();
    GeoJsonParser parser = new GeoJsonParser();


    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 3);

    assertEquals(3, parsedEvent.size());
    String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
    String parsedStringEventTwo = new String(parsedEvent.get(1), StandardCharsets.UTF_8);
    String parsedStringEventThree = new String(parsedEvent.get(2), StandardCharsets.UTF_8);

    assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[6.946535,51.437344]}," +
                 "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\","
                 + "\"publicationTime\":\"20161026212501\","
                 +
                 "\"lorryFlowRate\":\"5\",\"lorryAverageVehicleSpeed\":\"81.0\","
                 + "\"anyVehicleAverageVehicleSpeed\":\"81.0\","
                 +
                 "\"carAverageVehicleSpeed\":\"81.0\",\"anyVehicleFlowRate\":\"15\","
                 + "\"anyVehiclePercentageLongVehicle\":\"33.0\","
                 +
                 "\"carFlowRate\":\"10\",\"id\":\"fs.MQ_A40-10E_HFB_NO_1\"}}", parsedStringEventOne);
    assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[6.946535,51.437344]}," +
                 "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\","
                 + "\"publicationTime\":\"20161026212501\","
                 +
                 "\"lorryFlowRate\":\"0\",\"anyVehicleAverageVehicleSpeed\":\"107.0\","
                 + "\"carAverageVehicleSpeed\":\"107.0\","
                 +
                 "\"anyVehicleFlowRate\":\"6\",\"anyVehiclePercentageLongVehicle\":\"0.0\",\"carFlowRate\":\"6\"," +
                 "\"id\":\"fs.MQ_A40-10E_HFB_NO_2\"}}", parsedStringEventTwo);
    assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[7.545113,51.474907]}," +
                 "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\","
                 + "\"publicationTime\":\"20161026212501\","
                 +
                 "\"lorryFlowRate\":\"1\",\"lorryAverageVehicleSpeed\":\"71.0\","
                 + "\"anyVehicleAverageVehicleSpeed\":\"72.0\","
                 +
                 "\"carAverageVehicleSpeed\":\"73.0\",\"anyVehicleFlowRate\":\"9\","
                 + "\"anyVehiclePercentageLongVehicle\":\"11.0\""
                 +
                 ",\"carFlowRate\":\"8\",\"id\":\"fs.MQ_Bergh.09_HFB_SW_1\"}}", parsedStringEventThree);
  }

  private InputStream getInputStream(String s) {
    return IOUtils.toInputStream(s, "UTF-8");
  }

  private String getOneEventExampleMultiPolygon() {
    return """
        {
              "type": "Feature",
              "geometry": {
                "type": "MultiPolygon",
                "coordinates": [
                  [
                    [
                      [
                        30,
                        20
                      ],
                      [
                        45,
                        40
                      ],
                      [
                        10,
                        40
                      ],
                      [
                        30,
                        20
                      ]
                    ]
                  ],
                  [
                    [
                      [
                        15,
                        5
                      ],
                      [
                        40,
                        10
                      ],
                      [
                        10,
                        20
                      ],
                      [
                        5,
                        10
                      ],
                      [
                        15,
                        5
                      ]
                    ]
                  ]
                ]
              },
              "properties": {
                "measurementOrCalculationTime": "20180724160327",
                "publicationTime": "20161026212501",
                "lorryFlowRate": "4",
                "lorryAverageVehicleSpeed": "84.0",
                "anyVehicleAverageVehicleSpeed": "84.0",
                "carAverageVehicleSpeed": "85.0",
                "anyVehicleFlowRate": "21",
                "anyVehiclePercentageLongVehicle": "19.0",
                "carFlowRate": "17",
                "id": "fs.MQ_A40-10E_HFB_NO_1"
              }
            }""";
  }

  private String getOneEventExample() {
    return """
        {
        \t\t\t"type" : "Feature",
        \t\t\t"geometry" : {
        \t\t\t\t"type" : "Point",
        \t\t\t\t"coordinates" : [ 6.946535, 51.437344 ]
        \t\t\t},
        \t\t\t"properties" : {
        \t\t\t\t"measurementOrCalculationTime" : "20180717121027",
        \t\t\t\t"publicationTime" : "20161026212501",
        \t\t\t\t"lorryFlowRate" : "5",
        \t\t\t\t"lorryAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleAverageVehicleSpeed" : "81.0",
        \t\t\t\t"carAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleFlowRate" : "15",
        \t\t\t\t"anyVehiclePercentageLongVehicle" : "33.0",
        \t\t\t\t"carFlowRate" : "10",
        \t\t\t\t"id" : "fs.MQ_A40-10E_HFB_NO_1"
        \t\t\t}
        \t\t}""";
  }

  private String getFullExampleWithOneElement() {
    return """
        {
        \t"type" : "FeatureCollection",
        \t"name" : "geschwindigkeitsdaten_NRW",
        \t"features" : [
        \t\t{
        \t\t\t"type" : "Feature",
        \t\t\t"geometry" : {
        \t\t\t\t"type" : "Point",
        \t\t\t\t"coordinates" : [ 6.946535, 51.437344 ]
        \t\t\t},
        \t\t\t"properties" : {
        \t\t\t\t"measurementOrCalculationTime" : "20180717121027",
        \t\t\t\t"publicationTime" : "20161026212501",
        \t\t\t\t"lorryFlowRate" : "5",
        \t\t\t\t"lorryAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleAverageVehicleSpeed" : "81.0",
        \t\t\t\t"carAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleFlowRate" : "15",
        \t\t\t\t"anyVehiclePercentageLongVehicle" : "33.0",
        \t\t\t\t"carFlowRate" : "10",
        \t\t\t\t"id" : "fs.MQ_A40-10E_HFB_NO_1"
        \t\t\t}
        \t\t}
        \t
                 ]
        }
        """;
  }

  private String getFullExampleWithThreeElements() {
    return """
        {
        \t"type" : "FeatureCollection",
        \t"name" : "geschwindigkeitsdaten_NRW",
        \t"features" : [
        \t\t{
        \t\t\t"type" : "Feature",
        \t\t\t"geometry" : {
        \t\t\t\t"type" : "Point",
        \t\t\t\t"coordinates" : [ 6.946535, 51.437344 ]
        \t\t\t},
        \t\t\t"properties" : {
        \t\t\t\t"measurementOrCalculationTime" : "20180717121027",
        \t\t\t\t"publicationTime" : "20161026212501",
        \t\t\t\t"lorryFlowRate" : "5",
        \t\t\t\t"lorryAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleAverageVehicleSpeed" : "81.0",
        \t\t\t\t"carAverageVehicleSpeed" : "81.0",
        \t\t\t\t"anyVehicleFlowRate" : "15",
        \t\t\t\t"anyVehiclePercentageLongVehicle" : "33.0",
        \t\t\t\t"carFlowRate" : "10",
        \t\t\t\t"id" : "fs.MQ_A40-10E_HFB_NO_1"
        \t\t\t}
        \t\t},
        \t\t{
        \t\t\t"type" : "Feature",
        \t\t\t"geometry" : {
        \t\t\t\t"type" : "Point",
        \t\t\t\t"coordinates" : [ 6.946535, 51.437344 ]
        \t\t\t},
        \t\t\t"properties" : {
        \t\t\t\t"measurementOrCalculationTime" : "20180717121027",
        \t\t\t\t"publicationTime" : "20161026212501",
        \t\t\t\t"lorryFlowRate" : "0",
        \t\t\t\t"anyVehicleAverageVehicleSpeed" : "107.0",
        \t\t\t\t"carAverageVehicleSpeed" : "107.0",
        \t\t\t\t"anyVehicleFlowRate" : "6",
        \t\t\t\t"anyVehiclePercentageLongVehicle" : "0.0",
        \t\t\t\t"carFlowRate" : "6",
        \t\t\t\t"id" : "fs.MQ_A40-10E_HFB_NO_2"
        \t\t\t}
        \t\t},
        \t\t{
        \t\t\t"type" : "Feature",
        \t\t\t"geometry" : {
        \t\t\t\t"type" : "Point",
        \t\t\t\t"coordinates" : [ 7.545113, 51.474907 ]
        \t\t\t},
        \t\t\t"properties" : {
        \t\t\t\t"measurementOrCalculationTime" : "20180717121027",
        \t\t\t\t"publicationTime" : "20161026212501",
        \t\t\t\t"lorryFlowRate" : "1",
        \t\t\t\t"lorryAverageVehicleSpeed" : "71.0",
        \t\t\t\t"anyVehicleAverageVehicleSpeed" : "72.0",
        \t\t\t\t"carAverageVehicleSpeed" : "73.0",
        \t\t\t\t"anyVehicleFlowRate" : "9",
        \t\t\t\t"anyVehiclePercentageLongVehicle" : "11.0",
        \t\t\t\t"carFlowRate" : "8",
        \t\t\t\t"id" : "fs.MQ_Bergh.09_HFB_SW_1"
        \t\t\t}
        \t\t}
                 ]
        }
        """;
  }
}

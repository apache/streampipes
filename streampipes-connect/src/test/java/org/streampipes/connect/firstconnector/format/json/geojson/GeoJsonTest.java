/*
Copyright 2018 FZI Forschungszentrum Informatik

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

package org.streampipes.connect.firstconnector.format.json.geojson;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.streampipes.connect.firstconnector.format.geojson.GeoJsonParser;
import org.streampipes.model.schema.EventSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class GeoJsonTest {

    @Test
    public void getSchema1() throws UnsupportedEncodingException {
        GeoJsonParser parser = new GeoJsonParser();

        byte[] event = getOneEventExampleMultiPolygon().getBytes("UTF-8");

        EventSchema eventSchema = parser.getEventSchema(Collections.singletonList(event));

        assertEquals(11, eventSchema.getEventProperties().size());

    }

    @Test
    public void getSchema2() throws UnsupportedEncodingException {
        GeoJsonParser parser = new GeoJsonParser();

        byte[] event = getOneEventExample().getBytes("UTF-8");

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
                "[6.946535,51.437344]},\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\",\"publicationTime\"" +
                ":\"20161026212501\",\"lorryFlowRate\":\"5\",\"lorryAverageVehicleSpeed\":\"81.0\",\"anyVehicleAverageVehicleSpeed\"" +
                ":\"81.0\",\"carAverageVehicleSpeed\":\"81.0\",\"anyVehicleFlowRate\":\"15\",\"anyVehiclePercentageLongVehicle\"" +
                ":\"33.0\",\"carFlowRate\":\"10\",\"id\":\"fs.MQ_A40-10E_HFB_NO_1\"}}");
    }


    @Test
    public void parseThreeEvents() {

        String jo = getFullExampleWithThreeElements();
        GeoJsonParser parser = new GeoJsonParser();


        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 3);

        assertEquals(3, parsedEvent.size());
        String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
        String parsedStringEventTwo = new String(parsedEvent.get(1), StandardCharsets.UTF_8);
        String parsedStringEventThree = new String(parsedEvent.get(2), StandardCharsets.UTF_8);

        assertEquals( "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[6.946535,51.437344]}," +
                "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\",\"publicationTime\":\"20161026212501\"," +
                "\"lorryFlowRate\":\"5\",\"lorryAverageVehicleSpeed\":\"81.0\",\"anyVehicleAverageVehicleSpeed\":\"81.0\"," +
                "\"carAverageVehicleSpeed\":\"81.0\",\"anyVehicleFlowRate\":\"15\",\"anyVehiclePercentageLongVehicle\":\"33.0\"," +
                "\"carFlowRate\":\"10\",\"id\":\"fs.MQ_A40-10E_HFB_NO_1\"}}", parsedStringEventOne);
        assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[6.946535,51.437344]}," +
                "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\",\"publicationTime\":\"20161026212501\"," +
                "\"lorryFlowRate\":\"0\",\"anyVehicleAverageVehicleSpeed\":\"107.0\",\"carAverageVehicleSpeed\":\"107.0\"," +
                "\"anyVehicleFlowRate\":\"6\",\"anyVehiclePercentageLongVehicle\":\"0.0\",\"carFlowRate\":\"6\"," +
                "\"id\":\"fs.MQ_A40-10E_HFB_NO_2\"}}", parsedStringEventTwo);
        assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[7.545113,51.474907]}," +
                "\"properties\":{\"measurementOrCalculationTime\":\"20180717121027\",\"publicationTime\":\"20161026212501\"," +
                "\"lorryFlowRate\":\"1\",\"lorryAverageVehicleSpeed\":\"71.0\",\"anyVehicleAverageVehicleSpeed\":\"72.0\"," +
                "\"carAverageVehicleSpeed\":\"73.0\",\"anyVehicleFlowRate\":\"9\",\"anyVehiclePercentageLongVehicle\":\"11.0\"" +
                ",\"carFlowRate\":\"8\",\"id\":\"fs.MQ_Bergh.09_HFB_SW_1\"}}", parsedStringEventThree);
    }

    private InputStream getInputStream(String s) {

        try {
            return IOUtils.toInputStream(s, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getOneEventExampleMultiPolygon() {
        return  "{\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"MultiPolygon\",\n" +
                "        \"coordinates\": [\n" +
                "          [\n" +
                "            [\n" +
                "              [\n" +
                "                30,\n" +
                "                20\n" +
                "              ],\n" +
                "              [\n" +
                "                45,\n" +
                "                40\n" +
                "              ],\n" +
                "              [\n" +
                "                10,\n" +
                "                40\n" +
                "              ],\n" +
                "              [\n" +
                "                30,\n" +
                "                20\n" +
                "              ]\n" +
                "            ]\n" +
                "          ],\n" +
                "          [\n" +
                "            [\n" +
                "              [\n" +
                "                15,\n" +
                "                5\n" +
                "              ],\n" +
                "              [\n" +
                "                40,\n" +
                "                10\n" +
                "              ],\n" +
                "              [\n" +
                "                10,\n" +
                "                20\n" +
                "              ],\n" +
                "              [\n" +
                "                5,\n" +
                "                10\n" +
                "              ],\n" +
                "              [\n" +
                "                15,\n" +
                "                5\n" +
                "              ]\n" +
                "            ]\n" +
                "          ]\n" +
                "        ]\n" +
                "      },\n" +
                "      \"properties\": {\n" +
                "        \"measurementOrCalculationTime\": \"20180724160327\",\n" +
                "        \"publicationTime\": \"20161026212501\",\n" +
                "        \"lorryFlowRate\": \"4\",\n" +
                "        \"lorryAverageVehicleSpeed\": \"84.0\",\n" +
                "        \"anyVehicleAverageVehicleSpeed\": \"84.0\",\n" +
                "        \"carAverageVehicleSpeed\": \"85.0\",\n" +
                "        \"anyVehicleFlowRate\": \"21\",\n" +
                "        \"anyVehiclePercentageLongVehicle\": \"19.0\",\n" +
                "        \"carFlowRate\": \"17\",\n" +
                "        \"id\": \"fs.MQ_A40-10E_HFB_NO_1\"\n" +
                "      }\n" +
                "    }";
    }

    private String getOneEventExample() {
        return "{\n" +
                "\t\t\t\"type\" : \"Feature\",\n" +
                "\t\t\t\"geometry\" : {\n" +
                "\t\t\t\t\"type\" : \"Point\",\n" +
                "\t\t\t\t\"coordinates\" : [ 6.946535, 51.437344 ]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"properties\" : {\n" +
                "\t\t\t\t\"measurementOrCalculationTime\" : \"20180717121027\",\n" +
                "\t\t\t\t\"publicationTime\" : \"20161026212501\",\n" +
                "\t\t\t\t\"lorryFlowRate\" : \"5\",\n" +
                "\t\t\t\t\"lorryAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"carAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleFlowRate\" : \"15\",\n" +
                "\t\t\t\t\"anyVehiclePercentageLongVehicle\" : \"33.0\",\n" +
                "\t\t\t\t\"carFlowRate\" : \"10\",\n" +
                "\t\t\t\t\"id\" : \"fs.MQ_A40-10E_HFB_NO_1\"\n" +
                "\t\t\t}\n" +
                "\t\t}";
    }

    private String getFullExampleWithOneElement() {
        return "{\n" +
                "\t\"type\" : \"FeatureCollection\",\n" +
                "\t\"name\" : \"geschwindigkeitsdaten_NRW\",\n" +
                "\t\"features\" : [\n" +
                "\t\t{\n" +
                "\t\t\t\"type\" : \"Feature\",\n" +
                "\t\t\t\"geometry\" : {\n" +
                "\t\t\t\t\"type\" : \"Point\",\n" +
                "\t\t\t\t\"coordinates\" : [ 6.946535, 51.437344 ]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"properties\" : {\n" +
                "\t\t\t\t\"measurementOrCalculationTime\" : \"20180717121027\",\n" +
                "\t\t\t\t\"publicationTime\" : \"20161026212501\",\n" +
                "\t\t\t\t\"lorryFlowRate\" : \"5\",\n" +
                "\t\t\t\t\"lorryAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"carAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleFlowRate\" : \"15\",\n" +
                "\t\t\t\t\"anyVehiclePercentageLongVehicle\" : \"33.0\",\n" +
                "\t\t\t\t\"carFlowRate\" : \"10\",\n" +
                "\t\t\t\t\"id\" : \"fs.MQ_A40-10E_HFB_NO_1\"\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\n" +
                "         ]\n" +
                "}\n";
    }

    private String getFullExampleWithThreeElements() {
        return "{\n" +
                "\t\"type\" : \"FeatureCollection\",\n" +
                "\t\"name\" : \"geschwindigkeitsdaten_NRW\",\n" +
                "\t\"features\" : [\n" +
                "\t\t{\n" +
                "\t\t\t\"type\" : \"Feature\",\n" +
                "\t\t\t\"geometry\" : {\n" +
                "\t\t\t\t\"type\" : \"Point\",\n" +
                "\t\t\t\t\"coordinates\" : [ 6.946535, 51.437344 ]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"properties\" : {\n" +
                "\t\t\t\t\"measurementOrCalculationTime\" : \"20180717121027\",\n" +
                "\t\t\t\t\"publicationTime\" : \"20161026212501\",\n" +
                "\t\t\t\t\"lorryFlowRate\" : \"5\",\n" +
                "\t\t\t\t\"lorryAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"carAverageVehicleSpeed\" : \"81.0\",\n" +
                "\t\t\t\t\"anyVehicleFlowRate\" : \"15\",\n" +
                "\t\t\t\t\"anyVehiclePercentageLongVehicle\" : \"33.0\",\n" +
                "\t\t\t\t\"carFlowRate\" : \"10\",\n" +
                "\t\t\t\t\"id\" : \"fs.MQ_A40-10E_HFB_NO_1\"\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"type\" : \"Feature\",\n" +
                "\t\t\t\"geometry\" : {\n" +
                "\t\t\t\t\"type\" : \"Point\",\n" +
                "\t\t\t\t\"coordinates\" : [ 6.946535, 51.437344 ]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"properties\" : {\n" +
                "\t\t\t\t\"measurementOrCalculationTime\" : \"20180717121027\",\n" +
                "\t\t\t\t\"publicationTime\" : \"20161026212501\",\n" +
                "\t\t\t\t\"lorryFlowRate\" : \"0\",\n" +
                "\t\t\t\t\"anyVehicleAverageVehicleSpeed\" : \"107.0\",\n" +
                "\t\t\t\t\"carAverageVehicleSpeed\" : \"107.0\",\n" +
                "\t\t\t\t\"anyVehicleFlowRate\" : \"6\",\n" +
                "\t\t\t\t\"anyVehiclePercentageLongVehicle\" : \"0.0\",\n" +
                "\t\t\t\t\"carFlowRate\" : \"6\",\n" +
                "\t\t\t\t\"id\" : \"fs.MQ_A40-10E_HFB_NO_2\"\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"type\" : \"Feature\",\n" +
                "\t\t\t\"geometry\" : {\n" +
                "\t\t\t\t\"type\" : \"Point\",\n" +
                "\t\t\t\t\"coordinates\" : [ 7.545113, 51.474907 ]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"properties\" : {\n" +
                "\t\t\t\t\"measurementOrCalculationTime\" : \"20180717121027\",\n" +
                "\t\t\t\t\"publicationTime\" : \"20161026212501\",\n" +
                "\t\t\t\t\"lorryFlowRate\" : \"1\",\n" +
                "\t\t\t\t\"lorryAverageVehicleSpeed\" : \"71.0\",\n" +
                "\t\t\t\t\"anyVehicleAverageVehicleSpeed\" : \"72.0\",\n" +
                "\t\t\t\t\"carAverageVehicleSpeed\" : \"73.0\",\n" +
                "\t\t\t\t\"anyVehicleFlowRate\" : \"9\",\n" +
                "\t\t\t\t\"anyVehiclePercentageLongVehicle\" : \"11.0\",\n" +
                "\t\t\t\t\"carFlowRate\" : \"8\",\n" +
                "\t\t\t\t\"id\" : \"fs.MQ_Bergh.09_HFB_SW_1\"\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "         ]\n" +
                "}\n";
    }
}

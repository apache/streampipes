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

package org.apache.streampipes.connect.adapter.format.json.xml;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.adapter.format.xml.XmlParser;
import org.apache.streampipes.model.schema.EventSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlTest {

    @Test
    public void parseEventCarPark() throws AdapterException {

        String jo = getCarParkExample();

        XmlParser parser = new XmlParser("parkhaus");

        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

        assertEquals(6, parsedEvent.size());
        String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
        assertEquals(117, parsedStringEvent.length());
    }

    @Test
    public void parseEventParkingFaciltyStatus() throws AdapterException {

        String jo = getDatex2ParkingFacilityStatus();

        XmlParser parserStatus = new XmlParser("parkingAreaStatus");
        XmlParser parserFacility = new XmlParser("parkingFacilityStatus");


        List<byte[]> parsedEvent = parserStatus.parseNEvents(getInputStream(jo), 1);
        assertEquals(parsedEvent.size(), 2);
        String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
        assertEquals(321, parsedStringEvent.length());

        parsedEvent = parserFacility.parseNEvents(getInputStream(jo), 1);
        assertEquals(parsedEvent.size(), 10);
        parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
        assertEquals(383, parsedStringEvent.length());
    }

    @Test
    public void getSchemaCarExample() throws UnsupportedEncodingException {
        XmlParser parser = new XmlParser("parkhaus");

        byte[] event = getEventSchemaTest().getBytes("UTF-8");

        EventSchema eventSchema = parser.getEventSchema(Collections.singletonList(event));

        assertEquals(8, eventSchema.getEventProperties().size());

    }

    /*
    @Test
    public void getSchemaDatex2TrafficData1() throws UnsupportedEncodingException {
        XmlParser parser = new XmlParser("elaboratedData");

        List<byte[]> event = getEventSchemaTest2();

        EventSchema eventSchema = parser.getEventSchema(event);

        assertEquals(7, ((EventPropertyNested) ((EventPropertyNested) eventSchema.getEventProperties().get(0))
                .getEventProperties().get(0)).getEventProperties().size());

    }
    */

    /*

    @Test
    public void getSchemaDatex2TrafficData2() throws UnsupportedEncodingException {
        XmlParser parser = new XmlParser("elaboratedData");

        List<byte[]> event = getEventSchemaTest3();

        EventSchema eventSchema = parser.getEventSchema(event);

        assertEquals(5, ((EventPropertyNested) ((EventPropertyNested) eventSchema.getEventProperties().get(0))
                .getEventProperties().get(0)).getEventProperties().size());

    }
    */

    private InputStream getInputStream(String s) {

        try {
            return IOUtils.toInputStream(s, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getCarParkExample() {
        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                "<parkhaeuser xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>1</lfdnr>\n" +
                "        <bezeichnung>bahnhof.txt</bezeichnung>\n" +
                "        <gesamt>114</gesamt>\n" +
                "        <frei>000</frei>\n" +
                "        <status>1</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>3</tendenz>\n" +
                "    </parkhaus>\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>2</lfdnr>\n" +
                "        <bezeichnung>beethoven.txt</bezeichnung>\n" +
                "        <gesamt>416</gesamt>\n" +
                "        <frei>150</frei>\n" +
                "        <status>0</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>3</tendenz>\n" +
                "    </parkhaus>\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>3</lfdnr>\n" +
                "        <bezeichnung>friedensplatz.txt</bezeichnung>\n" +
                "        <gesamt>810</gesamt>\n" +
                "        <frei>295</frei>\n" +
                "        <status>0</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>1</tendenz>\n" +
                "    </parkhaus>\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>4</lfdnr>\n" +
                "        <bezeichnung>markt.txt</bezeichnung>\n" +
                "        <gesamt>305</gesamt>\n" +
                "        <frei>043</frei>\n" +
                "        <status>0</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>3</tendenz>\n" +
                "    </parkhaus>\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>5</lfdnr>\n" +
                "        <bezeichnung>muensterplatz.txt</bezeichnung>\n" +
                "        <gesamt>312</gesamt>\n" +
                "        <frei>069</frei>\n" +
                "        <status>0</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>1</tendenz>\n" +
                "    </parkhaus>\n" +
                "    <parkhaus>\n" +
                "        <lfdnr>6</lfdnr>\n" +
                "        <bezeichnung>stadthaus.txt</bezeichnung>\n" +
                "        <gesamt>320</gesamt>\n" +
                "        <frei>000</frei>\n" +
                "        <status>1</status>\n" +
                "        <zeitstempel>25.07.2018 10:45</zeitstempel>\n" +
                "        <tendenz>3</tendenz>\n" +
                "    </parkhaus>\n" +
                "</parkhaeuser>";
    }

    private String getDatex2ParkingFacilityStatus() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<d2LogicalModel modelBaseVersion=\"2\" extensionName=\"MDM\" extensionVersion=\"00-01-03\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://datex2.eu/schema/2/2_0 http://bast.s3.amazonaws.com/schema/1385369839450/MDM-Profile_ParkingFacilityStatus.xsd\" xmlns=\"http://datex2.eu/schema/2/2_0\">\n" +
                "    <exchange>\n" +
                "        <supplierIdentification>\n" +
                "            <country>de</country>\n" +
                "            <nationalIdentifier>DE-MDM-Kassel</nationalIdentifier>\n" +
                "        </supplierIdentification>\n" +
                "    </exchange>\n" +
                "    <payloadPublication xsi:type=\"GenericPublication\" lang=\"de\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <publicationTime>2018-07-25T13:17:16.776+02:00</publicationTime>\n" +
                "        <publicationCreator>\n" +
                "            <country>de</country>\n" +
                "            <nationalIdentifier>DE-MDM-Kassel</nationalIdentifier>\n" +
                "        </publicationCreator>\n" +
                "        <genericPublicationName>ParkingFacilityTableStatusPublication</genericPublicationName>\n" +
                "        <genericPublicationExtension>\n" +
                "            <parkingFacilityTableStatusPublication>\n" +
                "                <headerInformation>\n" +
                "                    <confidentiality>noRestriction</confidentiality>\n" +
                "                    <informationStatus>real</informationStatus>\n" +
                "                </headerInformation>\n" +
                "                <parkingAreaStatus>\n" +
                "                    <parkingAreaOccupancy>0.6244076</parkingAreaOccupancy>\n" +
                "                    <parkingAreaReference targetClass=\"ParkingArea\" id=\"1001[Stadtmitte]\" version=\"1.0\"/>\n" +
                "                    <parkingAreaStatusTime>2018-07-25T13:17:00.087+02:00</parkingAreaStatusTime>\n" +
                "                    <parkingAreaTotalNumberOfVacantParkingSpaces>1268</parkingAreaTotalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityLongTermOverride>3376</totalParkingCapacityLongTermOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>3376</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingAreaStatus>\n" +
                "                <parkingAreaStatus>\n" +
                "                    <parkingAreaOccupancy>0.37333333</parkingAreaOccupancy>\n" +
                "                    <parkingAreaReference targetClass=\"ParkingArea\" id=\"1002[Bhf. Wilhelmsh&#xf6;he]\" version=\"1.0\"/>\n" +
                "                    <parkingAreaStatusTime>2018-07-25T13:17:00.087+02:00</parkingAreaStatusTime>\n" +
                "                    <parkingAreaTotalNumberOfVacantParkingSpaces>94</parkingAreaTotalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityLongTermOverride>150</totalParkingCapacityLongTermOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>150</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingAreaStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.5590909</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"7[City Point]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>123</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>97</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>220</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>220</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>1.0</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"6[Theaterplatz]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>statusUnknown</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>228</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>0</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>228</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>228</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.6989796</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"1[Friedrichsplatz]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>685</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>295</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>980</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>980</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.3416149</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"4[Wilhelmsstra&#xdf;e]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>110</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>212</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>322</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>322</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.31129032</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"2[Kurf&#xfc;rsten Galerie]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>193</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>427</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>620</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>620</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>1.0</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"5[Rathaus]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>closed</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>298</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>0</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>298</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>298</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.48085105</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"10[Galeria Kaufhof]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>113</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>122</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>235</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>235</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.19469027</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"9[Martinskirche]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>22</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>91</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>113</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>113</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.93333334</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"3[Garde-du-Corps]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>336</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>24</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>360</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>360</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "                <parkingFacilityStatus>\n" +
                "                    <parkingFacilityOccupancy>0.37333333</parkingFacilityOccupancy>\n" +
                "                    <parkingFacilityReference targetClass=\"ParkingFacility\" id=\"8[Atrium]\" version=\"1.0\"/>\n" +
                "                    <parkingFacilityStatus>open</parkingFacilityStatus>\n" +
                "                    <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>\n" +
                "                    <totalNumberOfOccupiedParkingSpaces>56</totalNumberOfOccupiedParkingSpaces>\n" +
                "                    <totalNumberOfVacantParkingSpaces>94</totalNumberOfVacantParkingSpaces>\n" +
                "                    <totalParkingCapacityOverride>150</totalParkingCapacityOverride>\n" +
                "                    <totalParkingCapacityShortTermOverride>150</totalParkingCapacityShortTermOverride>\n" +
                "                </parkingFacilityStatus>\n" +
                "            </parkingFacilityTableStatusPublication>\n" +
                "        </genericPublicationExtension>\n" +
                "    </payloadPublication>\n" +
                "</d2LogicalModel>";
    }

    private String getEventSchemaTest() {
        return "{\"parkingFacilityReference\":{\"targetClass\":\"ParkingFacility\",\"id\"" +
                ":\"7[City Point]\",\"version\":1.0},\"parkingFacilityStatus\":\"open\"," +
                "\"totalNumberOfOccupiedParkingSpaces\":123,\"totalNumberOfVacantParkingSpaces\":97," +
                "\"totalParkingCapacityShortTermOverride\":220,\"totalParkingCapacityOverride\":220," +
                "\"parkingFacilityStatusTime\":\"2018-07-25T13:17:00.087+02:00\",\"parkingFacilityOccupancy\":0.5590909}";
    }

    private List<byte[]> getEventSchemaTest2() throws UnsupportedEncodingException {
        List<byte[]> list = new ArrayList();

        String dataone = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";
        String datatwo = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":1},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"lorry\"}}}}";
        String datathree = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"percentageLongVehicles\":{\"percentage\":7},\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":13},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"anyVehicle\"}}}}";
        String datafour = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"averageVehicleSpeed\":{\"speed\":44},\"xsi:type\":\"TrafficSpeed\",\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}\"\n";
        String datafive = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"averageVehicleSpeed\":{\"speed\":29},\"xsi:type\":\"TrafficSpeed\",\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"lorry\"}}}}";

        list.add(dataone.getBytes("UTF-8"));
        list.add(datatwo.getBytes("UTF-8"));
        list.add(datathree.getBytes("UTF-8"));
        list.add(datafour.getBytes("UTF-8"));
        list.add(datafive.getBytes("UTF-8"));

        return list;
    }

    private List<byte[]> getEventSchemaTest3() throws UnsupportedEncodingException {
        List<byte[]> list = new ArrayList();

        String dataone = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";
        String datatwo = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";
        String datathree = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";
        String datafour = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";
        String datafive = "{\"elaboratedData\":{\"basicData\":{\"measurementOrCalculationTime\":\"2018-07-25T16:21:27.365+02:00\",\"xsi:type\":\"TrafficFlow\",\"vehicleFlow\":{\"vehicleFlowRate\":12},\"pertinentLocation\":{\"xsi:type\":\"LocationByReference\",\"predefinedLocationReference\":{\"targetClass\":\"PredefinedLocation\",\"id\":\"fs.MQ_555.050_AB_SW_R_1\",\"version\":201610261425}},\"forVehiclesWithCharacteristicsOf\":{\"vehicleType\":\"car\"}}}}";

        list.add(dataone.getBytes("UTF-8"));
        list.add(datatwo.getBytes("UTF-8"));
        list.add(datathree.getBytes("UTF-8"));
        list.add(datafour.getBytes("UTF-8"));
        list.add(datafive.getBytes("UTF-8"));

        return list;
    }
}

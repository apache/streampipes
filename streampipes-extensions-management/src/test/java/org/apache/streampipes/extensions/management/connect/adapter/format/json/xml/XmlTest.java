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

package org.apache.streampipes.extensions.management.connect.adapter.format.json.xml;

import org.apache.streampipes.extensions.management.connect.adapter.format.xml.XmlParser;
import org.apache.streampipes.model.schema.EventSchema;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlTest {

  @Test
  public void parseEventCarPark() {

    String jo = getCarParkExample();

    XmlParser parser = new XmlParser("parkhaus");

    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

    assertEquals(6, parsedEvent.size());
    String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
    assertEquals(117, parsedStringEvent.length());
  }

  @Test
  public void parseEventParkingFaciltyStatus() {

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
  public void getSchemaCarExample() {
    XmlParser parser = new XmlParser("parkhaus");

    byte[] event = getEventSchemaTest().getBytes(StandardCharsets.UTF_8);

    EventSchema eventSchema = parser.getEventSchema(Collections.singletonList(event));

    assertEquals(8, eventSchema.getEventProperties().size());

  }

  private InputStream getInputStream(String s) {
    return IOUtils.toInputStream(s, "UTF-8");
  }

  @SuppressWarnings("checkstyle:LineLength")
  private String getCarParkExample() {
    return """
        <?xml version="1.0" encoding="iso-8859-1"?>
        <parkhaeuser xmlns:msdata="urn:schemas-microsoft-com:xml-msdata" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <parkhaus>
                <lfdnr>1</lfdnr>
                <bezeichnung>bahnhof.txt</bezeichnung>
                <gesamt>114</gesamt>
                <frei>000</frei>
                <status>1</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>3</tendenz>
            </parkhaus>
            <parkhaus>
                <lfdnr>2</lfdnr>
                <bezeichnung>beethoven.txt</bezeichnung>
                <gesamt>416</gesamt>
                <frei>150</frei>
                <status>0</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>3</tendenz>
            </parkhaus>
            <parkhaus>
                <lfdnr>3</lfdnr>
                <bezeichnung>friedensplatz.txt</bezeichnung>
                <gesamt>810</gesamt>
                <frei>295</frei>
                <status>0</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>1</tendenz>
            </parkhaus>
            <parkhaus>
                <lfdnr>4</lfdnr>
                <bezeichnung>markt.txt</bezeichnung>
                <gesamt>305</gesamt>
                <frei>043</frei>
                <status>0</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>3</tendenz>
            </parkhaus>
            <parkhaus>
                <lfdnr>5</lfdnr>
                <bezeichnung>muensterplatz.txt</bezeichnung>
                <gesamt>312</gesamt>
                <frei>069</frei>
                <status>0</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>1</tendenz>
            </parkhaus>
            <parkhaus>
                <lfdnr>6</lfdnr>
                <bezeichnung>stadthaus.txt</bezeichnung>
                <gesamt>320</gesamt>
                <frei>000</frei>
                <status>1</status>
                <zeitstempel>25.07.2018 10:45</zeitstempel>
                <tendenz>3</tendenz>
            </parkhaus>
        </parkhaeuser>""";
  }

  @SuppressWarnings({"checkstyle:LineLength"})
  private String getDatex2ParkingFacilityStatus() {
    return """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <d2LogicalModel modelBaseVersion="2" extensionName="MDM" extensionVersion="00-01-03" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://datex2.eu/schema/2/2_0 http://bast.s3.amazonaws.com/schema/1385369839450/MDM-Profile_ParkingFacilityStatus.xsd" xmlns="http://datex2.eu/schema/2/2_0">
            <exchange>
                <supplierIdentification>
                    <country>de</country>
                    <nationalIdentifier>DE-MDM-Kassel</nationalIdentifier>
                </supplierIdentification>
            </exchange>
            <payloadPublication xsi:type="GenericPublication" lang="de" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <publicationTime>2018-07-25T13:17:16.776+02:00</publicationTime>
                <publicationCreator>
                    <country>de</country>
                    <nationalIdentifier>DE-MDM-Kassel</nationalIdentifier>
                </publicationCreator>
                <genericPublicationName>ParkingFacilityTableStatusPublication</genericPublicationName>
                <genericPublicationExtension>
                    <parkingFacilityTableStatusPublication>
                        <headerInformation>
                            <confidentiality>noRestriction</confidentiality>
                            <informationStatus>real</informationStatus>
                        </headerInformation>
                        <parkingAreaStatus>
                            <parkingAreaOccupancy>0.6244076</parkingAreaOccupancy>
                            <parkingAreaReference targetClass="ParkingArea" id="1001[Stadtmitte]" version="1.0"/>
                            <parkingAreaStatusTime>2018-07-25T13:17:00.087+02:00</parkingAreaStatusTime>
                            <parkingAreaTotalNumberOfVacantParkingSpaces>1268</parkingAreaTotalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityLongTermOverride>3376</totalParkingCapacityLongTermOverride>
                            <totalParkingCapacityShortTermOverride>3376</totalParkingCapacityShortTermOverride>
                        </parkingAreaStatus>
                        <parkingAreaStatus>
                            <parkingAreaOccupancy>0.37333333</parkingAreaOccupancy>
                            <parkingAreaReference targetClass="ParkingArea" id="1002[Bhf. Wilhelmsh&#xf6;he]" version="1.0"/>
                            <parkingAreaStatusTime>2018-07-25T13:17:00.087+02:00</parkingAreaStatusTime>
                            <parkingAreaTotalNumberOfVacantParkingSpaces>94</parkingAreaTotalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityLongTermOverride>150</totalParkingCapacityLongTermOverride>
                            <totalParkingCapacityShortTermOverride>150</totalParkingCapacityShortTermOverride>
                        </parkingAreaStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.5590909</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="7[City Point]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>123</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>97</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>220</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>220</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>1.0</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="6[Theaterplatz]" version="1.0"/>
                            <parkingFacilityStatus>statusUnknown</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>228</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>0</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>228</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>228</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.6989796</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="1[Friedrichsplatz]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>685</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>295</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>980</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>980</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.3416149</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="4[Wilhelmsstra&#xdf;e]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>110</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>212</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>322</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>322</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.31129032</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="2[Kurf&#xfc;rsten Galerie]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>193</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>427</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>620</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>620</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>1.0</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="5[Rathaus]" version="1.0"/>
                            <parkingFacilityStatus>closed</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>298</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>0</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>298</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>298</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.48085105</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="10[Galeria Kaufhof]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>113</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>122</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>235</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>235</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.19469027</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="9[Martinskirche]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>22</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>91</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>113</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>113</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.93333334</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="3[Garde-du-Corps]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>336</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>24</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>360</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>360</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                        <parkingFacilityStatus>
                            <parkingFacilityOccupancy>0.37333333</parkingFacilityOccupancy>
                            <parkingFacilityReference targetClass="ParkingFacility" id="8[Atrium]" version="1.0"/>
                            <parkingFacilityStatus>open</parkingFacilityStatus>
                            <parkingFacilityStatusTime>2018-07-25T13:17:00.087+02:00</parkingFacilityStatusTime>
                            <totalNumberOfOccupiedParkingSpaces>56</totalNumberOfOccupiedParkingSpaces>
                            <totalNumberOfVacantParkingSpaces>94</totalNumberOfVacantParkingSpaces>
                            <totalParkingCapacityOverride>150</totalParkingCapacityOverride>
                            <totalParkingCapacityShortTermOverride>150</totalParkingCapacityShortTermOverride>
                        </parkingFacilityStatus>
                    </parkingFacilityTableStatusPublication>
                </genericPublicationExtension>
            </payloadPublication>
        </d2LogicalModel>""";
  }

  private String getEventSchemaTest() {
    return "{\"parkingFacilityReference\":{\"targetClass\":\"ParkingFacility\",\"id\""
           + ":\"7[City Point]\",\"version\":1.0},\"parkingFacilityStatus\":\"open\","
           + "\"totalNumberOfOccupiedParkingSpaces\":123,\"totalNumberOfVacantParkingSpaces\":97,"
           + "\"totalParkingCapacityShortTermOverride\":220,\"totalParkingCapacityOverride\":220,"
           + "\"parkingFacilityStatusTime\":\"2018-07-25T13:17:00.087+02:00\",\"parkingFacilityOccupancy\":0.5590909}";
  }

}

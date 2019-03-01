/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.management;

import org.junit.Test;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectParser;
import org.streampipes.connect.adapter.generic.format.xml.XmlFormat;
import org.streampipes.connect.adapter.generic.format.xml.XmlParser;
import org.streampipes.connect.adapter.generic.protocol.set.HttpProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.KafkaProtocol;
import org.streampipes.connect.adapter.specific.sensemap.OpenSenseMapAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.streampipes.rest.shared.util.JsonLdUtils;

import java.util.Collections;

import static org.junit.Assert.*;

public class AdapterDeserializerTest {

    @Test
    public void getGenericAdapterStreamDescription() throws AdapterException {
        AdapterDescription genericAdapterStreamDescription = new GenericAdapterStreamDescription();
        String jsonLd = JsonLdUtils.toJsonLD(genericAdapterStreamDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertTrue(a instanceof GenericAdapterStreamDescription);
        assertEquals(GenericAdapterStreamDescription.ID, a.getUri());
    }

    @Test
    public void getGenericAdapterSetDescription() throws AdapterException {
        AdapterDescription genericAdapterSetDescription = new GenericAdapterSetDescription();
        String jsonLd = JsonLdUtils.toJsonLD(genericAdapterSetDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertTrue(a instanceof GenericAdapterSetDescription);
        assertEquals(GenericAdapterSetDescription.ID, a.getUri());
    }

    @Test
    public void getSpecificAdapterStreamDescription() throws AdapterException {
        AdapterDescription specificAdapterStreamDescription = new OpenSenseMapAdapter().declareModel();
        String jsonLd = JsonLdUtils.toJsonLD(specificAdapterStreamDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertTrue(a instanceof SpecificAdapterStreamDescription);
        assertEquals(OpenSenseMapAdapter.ID, a.getUri());
    }

    @Test
    public void getFormatDescriptionHttpProtocolXmlFormat() throws AdapterException {
        AdapterDescription genericAdapterStreamDescription = new GenericAdapterStreamDescription();

        XmlFormat xmlFormat = new XmlFormat("row");
        HttpProtocol httpProtocol = new HttpProtocol(new XmlParser("row"), xmlFormat, "URL");

        FormatDescription formatDescription = xmlFormat.declareModel();
        ProtocolDescription protocolDescription = httpProtocol.declareModel();


        ((GenericAdapterStreamDescription) genericAdapterStreamDescription).setProtocolDescription(protocolDescription);
        ((GenericAdapterStreamDescription) genericAdapterStreamDescription).setFormatDescription(formatDescription);

        String jsonLd = JsonLdUtils.toJsonLD(genericAdapterStreamDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertNotNull(((GenericAdapterStreamDescription) a).getFormatDescription());
    }

    @Test
    public void getFormatDescriptionKafkaProtocolJsobObjectFormat() throws AdapterException {
        AdapterDescription genericAdapterStreamDescription = new GenericAdapterStreamDescription();

        JsonObjectFormat jsonObjectFormat = new JsonObjectFormat();
        KafkaProtocol kafkaProtocol = new KafkaProtocol(new JsonObjectParser(), jsonObjectFormat, "URL", "broker");

        FormatDescription formatDescription = jsonObjectFormat.declareModel();
        ProtocolDescription protocolDescription = kafkaProtocol.declareModel();

        ((GenericAdapterStreamDescription) genericAdapterStreamDescription).setProtocolDescription(protocolDescription);
        ((GenericAdapterStreamDescription) genericAdapterStreamDescription).setFormatDescription(formatDescription);

        String jsonLd = JsonLdUtils.toJsonLD(genericAdapterStreamDescription);

        System.out.println(jsonLd);
        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertNotNull(((GenericAdapterStreamDescription) a).getFormatDescription());
    }

    @Test
    public void testAdapterSetDeserialization() throws AdapterException {
        String jsonld = getGenericAdapterSetDescripionGeneratedByFrontend();
        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonld);


        assertNotNull(((GenericAdapterSetDescription) a).getFormatDescription());
        assertNotNull(((GenericAdapterSetDescription) a).getProtocolDescription());

    }

    @Test
    public void getRuleDescription() throws AdapterException {
        GenericAdapterStreamDescription genericAdapterStreamDescription = new GenericAdapterStreamDescription();

        JsonObjectFormat jsonObjectFormat = new JsonObjectFormat();
        KafkaProtocol kafkaProtocol = new KafkaProtocol(new JsonObjectParser(), jsonObjectFormat, "URL", "broker");
        UnitTransformRuleDescription unitTransformRuleDescription = new UnitTransformRuleDescription("key","Degree Celsius", "Kelvin");

        FormatDescription formatDescription = jsonObjectFormat.declareModel();
        ProtocolDescription protocolDescription = kafkaProtocol.declareModel();

        genericAdapterStreamDescription.setProtocolDescription(protocolDescription);
        genericAdapterStreamDescription.setFormatDescription(formatDescription);
        genericAdapterStreamDescription.setRules(Collections.singletonList(unitTransformRuleDescription));

        String jsonLd = JsonLdUtils.toJsonLD(genericAdapterStreamDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertEquals(1, (a.getRules().size()));
        assertEquals("Degree Celsius", ((UnitTransformRuleDescription) (a).getRules().get(0)).getFromUnitRessourceURL());
        assertEquals("Kelvin", ((UnitTransformRuleDescription) (a).getRules().get(0)).getToUnitRessourceURL());
    }

    private String getGenericAdapterSetDescripionGeneratedByFrontend() {
        return "{  \n" +
                        "    \"@context\":{  \n" +
                        "        \"sp\":\"https://streampipes.org/vocabulary/v1/\",\n" +
                        "        \"spi\":\"urn:streampipes.org:spi:\",\n" +
                        "        \"foaf\":\"http://xmlns.com/foaf/0.1/\"\n" +
                        "    },\n" +
                        "    \"@graph\":[  \n" +
                        "        {  \n" +
                        "            \"@id\":\"http://streampipes.org/dataset/69165e7e-e837-2b48-455b-909be67abb7e\",\n" +
                        "            \"@type\":\"sp:DataSet\"\n" +
                        "        },\n" +
                        "        {  \n" +
                        "            \"@id\":\"http://streampipes.org/genericadaptersetdescription\",\n" +
                        "            \"@type\":\"sp:GenericAdapterSetDescription\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the description for the http protocol\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#label\":\"HTTP (Set)\",\n" +
                        "            \"sp:config\":[  \n" +
                        "\n" +
                        "            ],\n" +
                        "            \"sp:hasDataSet\":{  \n" +
                        "                \"@id\":\"http://streampipes.org/dataset/69165e7e-e837-2b48-455b-909be67abb7e\"\n" +
                        "            },\n" +
                        "            \"sp:hasFormat\":{  \n" +
                        "                \"@id\":\"sp:format/xml\"\n" +
                        "            },\n" +
                        "            \"sp:hasProtocol\":{  \n" +
                        "                \"@id\":\"sp:protocol/set/http\"\n" +
                        "            },\n" +
                        "            \"sp:hasUri\":\"http://streampipes.org/genericadaptersetdescription\"\n" +
                        "        },\n" +
                        "        {  \n" +
                        "            \"@id\":\"sp:format/xml\",\n" +
                        "            \"@type\":\"sp:FormatDescription\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the description for the XML format\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#label\":\"XML\",\n" +
                        "            \"sp:config\":{  \n" +
                        "                \"@id\":\"spi:freetextstaticproperty:CEVrUb\"\n" +
                        "            },\n" +
                        "            \"sp:hasUri\":\"https://streampipes.org/vocabulary/v1/format/xml\"\n" +
                        "        },\n" +
                        "        {  \n" +
                        "            \"@id\":\"sp:protocol/set/http\",\n" +
                        "            \"@type\":\"sp:ProtocolDescription\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the description for the http protocol\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#label\":\"HTTP (Set)\",\n" +
                        "            \"sp:config\":{  \n" +
                        "                \"@id\":\"spi:freetextstaticproperty:dbiWfF\"\n" +
                        "            },\n" +
                        "            \"sp:hasUri\":\"https://streampipes.org/vocabulary/v1/protocol/set/http\",\n" +
                        "            \"sp:sourceType\":\"SET\"\n" +
                        "        },\n" +
                        "        {  \n" +
                        "            \"@id\":\"spi:freetextstaticproperty:CEVrUb\",\n" +
                        "            \"@type\":\"sp:FreeTextStaticProperty\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#description\":\"The Tag name of the events\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#label\":\"Tag\",\n" +
                        "            \"sp:hasValue\":\"asd\",\n" +
                        "            \"sp:internalName\":\"tag\",\n" +
                        "            \"sp:requiredDomainProperty\":\"\"\n" +
                        "        },\n" +
                        "        {  \n" +
                        "            \"@id\":\"spi:freetextstaticproperty:dbiWfF\",\n" +
                        "            \"@type\":\"sp:FreeTextStaticProperty\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#description\":\"This property defines the URL for the http request.\",\n" +
                        "            \"http://www.w3.org/2000/01/rdf-schema#label\":\"url\",\n" +
                        "            \"sp:hasValue\":\"sdf\",\n" +
                        "            \"sp:internalName\":\"url\",\n" +
                        "            \"sp:requiredDomainProperty\":\"\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";
    }
}
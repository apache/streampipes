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
import org.streampipes.connect.adapter.specific.twitter.TwitterAdapter;
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
        AdapterDescription specificAdapterStreamDescription = new TwitterAdapter().declareModel();
        String jsonLd = JsonLdUtils.toJsonLD(specificAdapterStreamDescription);

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertTrue(a instanceof SpecificAdapterStreamDescription);
        assertEquals(TwitterAdapter.ID, a.getUri());
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

        AdapterDescription a = AdapterDeserializer.getAdapterDescription(jsonLd);

        assertNotNull(((GenericAdapterStreamDescription) a).getFormatDescription());
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
        assertEquals("Degree Celsius", ((UnitTransformRuleDescription) (a).getRules().get(0)).getFromUnit());
        assertEquals("Kelvin", ((UnitTransformRuleDescription) (a).getRules().get(0)).getToUnit());
    }


}
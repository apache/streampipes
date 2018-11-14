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

package org.streampipes.connect.adapter.generic.guess;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.streampipes.connect.adapter.generic.Mock;
import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class SchemaGuesserTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(Mock.PORT));

    @Before
    public void setPort() {
        SchemaGuesser.port = Mock.PORT;
    }

    @Test
    public void requestProbabilitiesStringTest() {

        String expected = "{\"result\": []}";

        stubFor(post(urlEqualTo("/predict"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expected)));

        SchemaGuesser schemaGuesser = new SchemaGuesser();

        Double[] data = {1.1, 2.0};
        String result = schemaGuesser.requestProbabilitiesString(data);

        assertEquals(expected, result);

    }

    @Test
    public void requestProbabilitiesStringEndpointNotAvailableTest() {

        String expected = "{\"result\": []}";

        stubFor(post(urlEqualTo("/predict"))
                .willReturn(aResponse()
                        .withStatus(404)));

        SchemaGuesser schemaGuesser = new SchemaGuesser();

        Double[] data = {1.1, 2.0};
        String result = schemaGuesser.requestProbabilitiesString(data);

        assertEquals(expected, result);

    }

    @Test
    public void getDomainPropertyProbabilityTest() {

        String payload = "{\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"class\": \"one\", \n" +
                "      \"probability\": 1.1\n" +
                "    }]}";

        stubFor(post(urlEqualTo("/predict"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(payload)));


        String[] data = {"a"};
        DomainPropertyProbabilityList result = SchemaGuesser.getDomainPropertyProbability(data);

        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        assertEquals("one", result.getList().get(0).getDomainProperty());
        assertEquals("1.1", result.getList().get(0).getProbability());
    }

    @Test
    public void requestProbabilitiesObjectTest() {

        String payload = "{\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"class\": \"one\", \n" +
                "      \"probability\": 1\n" +
                "    }]}";

        stubFor(post(urlEqualTo("/predict"))
                        .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(payload)
                                ));


        SchemaGuesser schemaGuesser = new SchemaGuesser();

        Double[] data = {1.1};
        PropertyGuessResults result = schemaGuesser.requestProbabilitiesObject(data);

        assertNotNull(result.getResult());
        assertEquals(1, result.getResult().length);
        assertEquals("one", result.getResult()[0].getClazz());
        assertEquals(1, result.getResult()[0].getProbability(), 0.0);
    }


}
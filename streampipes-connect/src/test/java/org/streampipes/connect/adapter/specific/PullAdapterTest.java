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

package org.streampipes.connect.adapter.specific;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.streampipes.connect.exception.AdapterException;

public class PullAdapterTest {
    private int port = 4082;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);


    @Test
    public void getDataFromEndpointStringSuccess() throws AdapterException {
        String expected = "EXPECTED_STRING";

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expected)));

        String result = PullRestAdapter.getDataFromEndpointString("http://localhost:" + port + "/");

        assertEquals(expected, result);

    }

    @Test(expected = AdapterException.class)
    public void getDataFromEndpointStringFail() throws AdapterException {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("")));

        PullRestAdapter.getDataFromEndpointString("http://localhost:" + port + "/");

    }
}
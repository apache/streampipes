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

package org.streampipes.connect.rest.master;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.GuessManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.connect.utils.Utils;
import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.vocabulary.StreamPipes;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GuessResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/master/guess";
    }

    private GuessResource guessResource;
    private GuessManagement guessManagement;
    private Server server;

    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        guessResource = new GuessResource();
        server = getMasterServer(guessResource);

        guessManagement = mock(GuessManagement.class);
        guessResource.setGuessManagement(guessManagement);
    }

    @After
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void guessSchemaSuccess() {
        GuessSchema guessSchema = getGuessSchema();
        when(guessManagement.guessSchema(any())).thenReturn(guessSchema);
        String data = Utils.getMinimalStreamAdapterJsonLD();

         Response res = given()
                .body(data)
                .when()
                .post(getApi() + "/schema");

            res.then()
                .assertThat()
                .statusCode(200);

        String resultString = res.body().print();

        GuessSchema resultObject = JsonLdUtils.fromJsonLd(resultString, GuessSchema.class);

        assertNotNull(resultObject);
        assertNotNull(resultObject.propertyProbabilityList);
        assertEquals(0, resultObject.propertyProbabilityList.size());
        assertNotNull(resultObject.eventSchema);
        assertNotNull(resultObject.eventSchema.getEventProperties());
        assertEquals(1, resultObject.eventSchema.getEventProperties().size());
        assertEquals("id", resultObject.eventSchema.getEventProperties().get(0).getRuntimeName());
    }

    private GuessSchema getGuessSchema() {
        EventSchema eventSchema = new EventSchema();
        EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
        eventPropertyPrimitive.setRuntimeType("http://schema.org/Number");
        eventPropertyPrimitive.setRuntimeName("id");



        eventSchema.setEventProperties(Arrays.asList(eventPropertyPrimitive));
        GuessSchema guessSchema = new GuessSchema();
        guessSchema.setEventSchema(eventSchema);
        guessSchema.setPropertyProbabilityList(Arrays.asList(new DomainPropertyProbabilityList()));

        return guessSchema;

    }
}
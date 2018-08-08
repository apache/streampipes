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

package org.streampipes.connect.rest.worker;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponseOptions;
import com.jayway.restassured.specification.RequestSpecification;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.AdapterWorkerManagement;
import org.streampipes.connect.management.IAdapterWorkerManagement;
import org.streampipes.connect.rest.worker.WorkerResource;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.rest.shared.serializer.GsonClientModelProvider;
import org.streampipes.rest.shared.serializer.GsonWithIdProvider;
import org.streampipes.rest.shared.serializer.GsonWithoutIdProvider;
import org.streampipes.rest.shared.serializer.JsonLdProvider;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


public class WorkerResourceTest {
    private static final String API_VERSION = "/api/v1/riemer@fzi.de/worker";
    private static final String ERROR_MESSAGE = "error";

    private WorkerResource adapterResource;

    private Server server;

    @Before
    public  void before() {
        Config.PORT = 8019;
        RestAssured.port = 8019;

        adapterResource = new WorkerResource();

        // TODO put somewhere else
        ResourceConfig config = new ResourceConfig().register(adapterResource)
                .register(GsonWithIdProvider.class)
                .register(GsonWithoutIdProvider.class)
                .register(GsonClientModelProvider.class)
                .register(JsonLdProvider.class);

        URI baseUri = UriBuilder
                .fromUri(Config.getBaseUrl())
                .build();

        server = JettyHttpContainerFactory.createServer(baseUri, config);

        IAdapterWorkerManagement adapterManagement = mock(AdapterWorkerManagement.class);
        adapterResource.setAdapterManagement(adapterManagement);

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
    public void invokeStreamAdapterSuccess() {

        String data = getMinimalStreamAdapterJsonLD();
        getSuccessRequest(data, "/stream/invoke", "Stream adapter with id http://t.de/ successfully started");

    }

    @Test
    public void invokeStreamAdapterFail() throws AdapterException {

        IAdapterWorkerManagement adapterManagement = mock(AdapterWorkerManagement.class);
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).invokeStreamAdapter(any(AdapterStreamDescription.class));
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        getFailRequest(data, "/stream/invoke");

    }

    @Test
    public void stopStreamAdapterSuccess() {

        String data = getMinimalStreamAdapterJsonLD();
        getSuccessRequest(data, "/stream/stop", "Stream adapter with id http://t.de/ successfully stopped");

    }

    @Test
    public void stopStreamAdapterFail() throws AdapterException {

        IAdapterWorkerManagement adapterManagement = mock(AdapterWorkerManagement.class);
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).stopStreamAdapter(any(AdapterStreamDescription.class));
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        getFailRequest(data, "/stream/stop");

    }


    @Test
    public void invokeSetAdapterSuccess() {
        String data = getMinimalStreamAdapterJsonLD();
        getSuccessRequest(data, "/set/invoke", "Set adapter with id http://t.de/ successfully started");
    }

    @Test
    public void invokeSetAdapterFail() throws AdapterException {

        IAdapterWorkerManagement adapterManagement = mock(AdapterWorkerManagement.class);
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).invokeSetAdapter(any(AdapterSetDescription.class));
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        getFailRequest(data, "/set/invoke");
    }

    @Test
    public void stopSetAdapterSuccess() {

        String data = getMinimalStreamAdapterJsonLD();
        getSuccessRequest(data, "/set/stop", "Set adapter with id http://t.de/ successfully stopped");
    }

    @Test
    public void stopSetAdapterFail() throws AdapterException {

        IAdapterWorkerManagement adapterManagement = mock(AdapterWorkerManagement.class);
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).stopSetAdapter(any(AdapterSetDescription.class));
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        getFailRequest(data, "/set/stop");
    }


    private String getMinimalStreamAdapterJsonLD() {
        return getMinimalAdapterJsonLD(new AdapterStreamDescription());
    }

    private String getMinimalSetAdapterJsonLD() {
        return getMinimalAdapterJsonLD(new AdapterSetDescription());
    }

    private String getMinimalAdapterJsonLD(AdapterDescription asd) {
        String id = "http://t.de/";
        asd.setUri(id);
        asd.setId(id);

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        try {
            return Utils.asString(jsonLdTransformer.toJsonLd(asd));
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return "";
    }

    private ValidatableResponseOptions getSuccessRequest(String data, String route, String responseMessage) {
        return  getRequest(data, route)
                .body("success", equalTo(true))
                .body("notifications[0].title", equalTo(responseMessage));
    }

    private ValidatableResponseOptions getFailRequest(String data, String route) {
        return  getRequest(data, route)
                .body("success", equalTo(false))
                .body("notifications[0].title", equalTo(ERROR_MESSAGE));
    }
    private ValidatableResponseOptions getRequest(String data, String route) {
        return given().contentType("application/ld+json")
                .body(data)
                .when()
                .post(API_VERSION + route)
                .then()
                .assertThat();
    }

}
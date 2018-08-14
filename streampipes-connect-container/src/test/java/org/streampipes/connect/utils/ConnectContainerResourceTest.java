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

package org.streampipes.connect.utils;

import java.net.URI;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponseOptions;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.rest.shared.serializer.GsonClientModelProvider;
import org.streampipes.rest.shared.serializer.GsonWithIdProvider;
import org.streampipes.rest.shared.serializer.GsonWithoutIdProvider;
import org.streampipes.rest.shared.serializer.JsonLdProvider;
import org.streampipes.rest.shared.util.JsonLdUtils;

import static org.hamcrest.core.IsEqual.equalTo;
import static com.jayway.restassured.RestAssured.given;

import javax.ws.rs.core.UriBuilder;

public abstract class ConnectContainerResourceTest {

    protected static final String ERROR_MESSAGE = "error";
    protected Server getServer(AbstractContainerResource resource) {
        ResourceConfig config = new ResourceConfig()
                .register(GsonWithIdProvider.class)
                .register(GsonWithoutIdProvider.class)
                .register(GsonClientModelProvider.class)
                .register(JsonLdProvider.class)
                .register(resource);

        URI baseUri = UriBuilder
                .fromUri(Config.getBaseUrl())
                .build();

        return JettyHttpContainerFactory.createServer(baseUri, config);
    }

    protected abstract String getApi();

    protected <T> T getJsonLdSucessRequest(String route, Class<T> clazz) {
        return getJsonLdSucessRequest(route, clazz, "");

    }

    protected <T> T getJsonLdSucessRequest(String route, Class<T> clazz, String rootElement) {
        Response response = given().contentType("application/ld+json")
                .when()
                .get(getApi() + route);

        response.then()
                .statusCode(200);

        String resultString = response.body().print();

        T resultObject;
        if (rootElement.equals("")) {
            resultObject = JsonLdUtils.fromJsonLd(resultString, clazz);
        } else {
            resultObject = JsonLdUtils.fromJsonLd(resultString, clazz, rootElement);
        }

        return resultObject;

    }

    protected ValidatableResponseOptions postJsonLdSuccessRequest(String data, String route, String responseMessage) {
        return  postJsonLdRequest(data, route)
                .body("success", equalTo(true))
                .body("notifications[0].title", equalTo(responseMessage));
    }

    protected ValidatableResponseOptions postJsonLdFailRequest(String data, String route) {
        return  postJsonLdRequest(data, route)
                .body("success", equalTo(false))
                .body("notifications[0].title", equalTo(ERROR_MESSAGE));
    }
    protected ValidatableResponseOptions postJsonLdRequest(String data, String route) {
        return given().contentType("application/ld+json")
                .body(data)
                .when()
                .post(getApi() + route)
                .then()
                .assertThat()
                .statusCode(200);
    }
}

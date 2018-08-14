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

package org.streampipes.connect.rest;


import com.jayway.restassured.RestAssured;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.utils.ConnectContainerResourceTest;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.core.IsEqual.equalTo;


public class WelcomePageTest extends ConnectContainerResourceTest {
    private Server server;

    @Before
    public void before() {
        Config.PORT = 8019;
        RestAssured.port = 8019;
//                Set<Class<?>> allClasses = new HashSet<>();
//
//        allClasses.add(WelcomePage.class);
//
//        ResourceConfig config = new ResourceConfig(allClasses);
//
//        URI baseUri = UriBuilder
//                .fromUri(Config.getBaseUrl())
//                .build();
//
//        server = JettyHttpContainerFactory.createServer(baseUri, config);

        WelcomePage welcomePage = new WelcomePage();
        server = getServer(welcomePage);
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
    public void getWelcomePageHtmlTest() {
        get("/").then().body("html.head.title", equalTo("StreamPipes Connector Container"));
        get("/").then().body("html.body.h1", equalTo("Connector Container with ID MAIN_CONTAINER is running"));
    }

    @Override
    protected String getApi() {
        return "";
    }
}
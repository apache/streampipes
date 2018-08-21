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
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.utils.ConnectContainerResourceTest;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.core.IsEqual.equalTo;

public class WelcomePageWorkerTest extends ConnectContainerResourceTest {


    private Server server;

    @Before
    public void before() {
        Config.WORKER_PORT = 8019;
        RestAssured.port = 8019;

        WelcomePageWorker welcomePage = new WelcomePageWorker("WORKER_01");
        server = getWorkerServer(welcomePage);
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
        get("/").then().body("html.head.title", equalTo("StreamPipes Connector Worker Container"));
        get("/").then().body("html.body.h1", equalTo("Worker Connector Container with ID: WORKER_01"));
    }

    @Override
    protected String getApi() {
        return "";
    }
}

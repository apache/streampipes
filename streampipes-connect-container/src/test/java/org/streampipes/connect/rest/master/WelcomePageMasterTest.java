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
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.management.master.IAdapterMasterManagement;
import org.streampipes.connect.management.master.WorkerRestClient;
import org.streampipes.connect.management.worker.AdapterWorkerManagement;
import org.streampipes.connect.rest.master.WelcomePageMaster;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.storage.couchdb.utils.CouchDbConfig;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WelcomePageMasterTest extends ConnectContainerResourceTest {
    private Server server;

    private WelcomePageMaster welcomePage;

    @Before
    public void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        welcomePage = new WelcomePageMaster();
        server = getMasterServer(welcomePage);
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
    public void getWelcomePageHtmlWithAdaptersSucessTest() throws AdapterException {
        testMainPart();
        get("/").then().body("html.body.ol.size()", equalTo(1));
        get("/").then().body("html.body.ol[0].toString()", equalTo("testId"));
    }

    private void testMainPart() throws AdapterException {
        String id = "testId";
        AdapterDescription adapterDescription = new GenericAdapterStreamDescription();
        adapterDescription.setAdapterId(id);
        AdapterMasterManagement adapterManagement = mock(AdapterMasterManagement.class);
        welcomePage.setAdapterMasterManagement(adapterManagement);
        when(adapterManagement.getAllAdapters(any())).thenReturn(Arrays.asList(adapterDescription));
        get("/").then().body("html.head.title", equalTo("StreamPipes Connector Master Container"));
        get("/").then().body("html.body.h1", equalTo("Connector Master Container"));
        get("/").then().body("html.body.h2", equalTo("All Running Adapters"));
    }

    @Override
    protected String getApi() {
        return "";
    }
}
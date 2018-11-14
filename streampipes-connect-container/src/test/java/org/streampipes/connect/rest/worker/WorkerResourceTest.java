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
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.worker.AdapterWorkerManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.connect.utils.Utils;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


public class WorkerResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/worker";
    }

    private WorkerResource workerResource;

    private Server server;

    private AdapterWorkerManagement adapterManagement;

    @Before
    public  void before() {
        Config.WORKER_PORT = 8019;
        RestAssured.port = 8019;

        workerResource = new WorkerResource();
        server = getWorkerServer(workerResource);

        adapterManagement = mock(AdapterWorkerManagement.class);
        workerResource.setAdapterManagement(adapterManagement);
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

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdSuccessRequest(data, "/stream/invoke", "Stream adapter with id http://t.de/ successfully started");

    }

    @Test
    public void invokeStreamAdapterFail() throws AdapterException {

        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).invokeStreamAdapter(any(AdapterStreamDescription.class));
        workerResource.setAdapterManagement(adapterManagement);

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdFailRequest(data,"/stream/invoke");

    }

    @Test
    public void stopStreamAdapterSuccess() {

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdSuccessRequest(data,"/stream/stop", "Stream adapter with id http://t.de/ successfully stopped");

    }

    @Test
    public void stopStreamAdapterFail() throws AdapterException {

        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).stopStreamAdapter(any(AdapterStreamDescription.class));
        workerResource.setAdapterManagement(adapterManagement);

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdFailRequest(data,"/stream/stop");

    }


    @Test
    public void invokeSetAdapterSuccess() {
        String data = Utils.getMinimalSetAdapterJsonLD();
        postJsonLdSuccessRequest(data,"/set/invoke", "Set adapter with id http://t.de/ successfully started");
    }

    @Test
    public void invokeSetAdapterFail() throws AdapterException {

        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).invokeSetAdapter(any(AdapterSetDescription.class));
        workerResource.setAdapterManagement(adapterManagement);

        String data = Utils.getMinimalSetAdapterJsonLD();
        postJsonLdFailRequest(data, "/set/invoke");
    }

    @Test
    public void stopSetAdapterSuccess() {

        String data = Utils.getMinimalSetAdapterJsonLD();
        postJsonLdSuccessRequest(data, "/set/stop", "Set adapter with id http://t.de/ successfully stopped");
    }

    @Test
    public void stopSetAdapterFail() throws AdapterException {

        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterManagement).stopSetAdapter(any(AdapterSetDescription.class));
        workerResource.setAdapterManagement(adapterManagement);

        String data = Utils.getMinimalSetAdapterJsonLD();
        postJsonLdFailRequest(data, "/set/stop");
    }


}
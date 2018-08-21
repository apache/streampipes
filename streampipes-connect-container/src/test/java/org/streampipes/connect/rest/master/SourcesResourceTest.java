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
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.ISourcesManagement;
import org.streampipes.connect.management.master.SourcesManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.SpDataSet;
import org.streampipes.rest.shared.util.JsonLdUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ WorkerRestClient.class })
public class SourcesResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/master/sources";
    }

    private Server server;

    private SourcesResource sourcesResource;

    private ISourcesManagement sourcesManagement;


    @Before
    public  void before() {
//        PowerMockito.mockStatic(WorkerRestClient.class);
        Config.PORT = 8019;
        RestAssured.port = 8019;

        sourcesResource = new SourcesResource("");
        server = getServer(sourcesResource);
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
    public void addAdapterSuccess() throws Exception {
        ISourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doNothing().when(sourcesManagement).addAdapter(any());
        sourcesResource.setSourcesManagement(sourcesManagement);

        String data = getMinimalDataSetJsonLd();
        postJsonLdSuccessRequest(data, "/id/streams", "Instance of data set http://dataset.de/1 successfully started");

        verify(sourcesManagement, times(1)).addAdapter(any());
    }

    @Test
    public void addAdapterFail() throws AdapterException {
        ISourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doThrow(AdapterException.class).when(sourcesManagement).addAdapter(any());
        sourcesResource.setSourcesManagement(sourcesManagement);

        String data = getMinimalDataSetJsonLd();
        postJsonLdFailRequest(data, "/id/streams", "Could not set data set instance: http://dataset.de/1");

    }

    @Test
    public void detachSuccess() throws AdapterException {
        ISourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doNothing().when(sourcesManagement).detachAdapter(anyString(), anyString());
        sourcesResource.setSourcesManagement(sourcesManagement);

        deleteJsonLdSucessRequest("/id0/streams/id1");

        verify(sourcesManagement, times(1)).detachAdapter(anyString(), anyString());
    }

    @Test
    public void detachFail() throws AdapterException {
        ISourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doThrow(AdapterException.class).when(sourcesManagement).detachAdapter(anyString(), anyString());
        sourcesResource.setSourcesManagement(sourcesManagement);

        deleteJsonLdFailRequest("/id0/streams/id1");
    }

    private String getMinimalDataSetJsonLd() {
        SpDataSet dataSet = new SpDataSet();
        dataSet.setUri("http://dataset.de/1");

        return JsonLdUtils.toJsonLD(dataSet);
    }
}
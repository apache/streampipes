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
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.connect.utils.Utils;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdapterResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/admin@fzi.de/master/adapters";
    }

    private AdapterResource adapterResource;

    private Server server;

    private AdapterMasterManagement adapterMasterManagement;

    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        adapterResource = new AdapterResource("");
        server = getMasterServer(adapterResource);

        adapterMasterManagement = mock(AdapterMasterManagement.class);
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);
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
    public void addAdapterSuccess() throws AdapterException {
        when(adapterMasterManagement.addAdapter(any(), anyString(), any(), anyString())).thenReturn("http://t.de/");
        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdSuccessRequest(data, "/", "http://t.de/");
    }

    @Test
    public void addAdapterFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterMasterManagement).addAdapter(any(), any(), any(), anyString());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdFailRequest(data,"/");
    }

    @Test
    public void getAdapterSuccess() throws AdapterException {
        AdapterMasterManagement adapterMasterManagement  = mock(AdapterMasterManagement.class);
        when(adapterMasterManagement.getAdapter(any(), any())).thenReturn(new GenericAdapterStreamDescription());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        AdapterDescription result = getJsonLdSucessRequest("/testid", GenericAdapterStreamDescription.class);

        assertEquals(GenericAdapterStreamDescription.ID, result.getUri());
    }

    @Test
    public void getAdapterFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterMasterManagement).getAdapter(anyString(), any());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        getJsonLdFailRequest("/testid");
    }

    @Test
    public void deleteAdapterSuccess() throws AdapterException {
        AdapterMasterManagement adapterMasterManagement  = mock(AdapterMasterManagement.class);
        doNothing().when(adapterMasterManagement).deleteAdapter(anyString(), anyString());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        deleteJsonLdSucessRequest("/testid");
    }

    @Test
    public void deleteAdapterFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterMasterManagement).deleteAdapter(anyString(), anyString());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        deleteJsonLdFailRequest("/testid");
    }

    // TODO
    // This test currently is not active. The problem is that we currently cannot deserialize the list with adapter
    // descriptions because AdpaterDesription is an abstract class and the concrete subclasses are not known.
    // Have a look at class org.streampipes.connect.management.AdapterDeserializer, which is a workaround for
    // AdapterDescriptions Objects
    //
//    @Test
//    public void getAllAdaptersSuccess() throws AdapterException {
//        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
//        IAdapterMasterManagement adapterMasterManagement  = mock(AdapterMasterManagement.class);
//        when(adapterMasterManagement.getAllAdapters(any())).thenReturn(adapterDescriptions);
//        adapterResource.setAdapterMasterManagement(adapterMasterManagement);
//
//        AdapterDescriptionList result = getJsonLdSucessRequest("/", AdapterDescriptionList.class, StreamPipes.ADAPTER_DESCRIPTION_LIST);
////        AdapterDescriptionList result = getJsonLdSucessRequest("/", AdapterDescriptionList.class);
//
//        assertEquals("http://streampipes.org/adapterlist", result.getUri());
//        assertEquals(1, result.getList().size());
//        assertEquals("http://t.id", result.getList().get(0).getUri());
//
//    }

    @Test
    public void getAllAdaptersFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterMasterManagement).getAllAdapters(any());
        adapterResource.setAdapterMasterManagement(adapterMasterManagement);

        getJsonLdFailRequest("/");
    }
}
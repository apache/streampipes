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
import org.streampipes.connect.management.master.AdapterTemplateMasterManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.connect.utils.Utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class AdapterTemplateResourceTest extends ConnectContainerResourceTest {

    private AdapterTemplateResource adapterTemplateResource;

    private Server server;

    private AdapterTemplateMasterManagement adapterTemplateMasterManagement;

    @Override
    protected String getApi() {
        return "/api/v1/admin@fzi.de/master/adapters/template";
    }

    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        adapterTemplateResource = new AdapterTemplateResource();
        server = getMasterServer(adapterTemplateResource);

        adapterTemplateMasterManagement = mock(AdapterTemplateMasterManagement.class);
        adapterTemplateResource.setAdapterTemplateMasterManagement(adapterTemplateMasterManagement);
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
    public void addAdapterTemplateSuccess() throws AdapterException {
        when(adapterTemplateMasterManagement.addAdapterTemplate(any())).thenReturn("id_0");
        String data = Utils.getMinimalStreamAdapterJsonLD();

        postJsonLdSuccessRequest(data, "/", "id_0");

        verify(adapterTemplateMasterManagement, times(1)).addAdapterTemplate(any());
    }

    @Test
    public void addAdapterTemplateFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterTemplateMasterManagement).addAdapterTemplate(any());
        adapterTemplateResource.setAdapterTemplateMasterManagement(adapterTemplateMasterManagement);

        String data = Utils.getMinimalStreamAdapterJsonLD();
        postJsonLdFailRequest(data,"/");
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
//        AdapterDescriptionList adapterDescriptionList = new AdapterDescriptionList();
//        adapterDescriptionList.setList(adapterDescriptions);
//        AdapterTemplateMasterManagement adapterTemplateMasterManagement  = mock(AdapterTemplateMasterManagement.class);
//        when(adapterTemplateMasterManagement.getAllAdapterTemplates()).thenReturn(adapterDescriptionList);
//        adapterTemplateResource.setAdapterTemplateMasterManagement(adapterTemplateMasterManagement);
//
////        AdapterDescriptionList result = getJsonLdSucessRequest("/all", AdapterDescriptionList.class, StreamPipes.ADAPTER_DESCRIPTION_LIST);
//        AdapterDescriptionList result = getJsonLdSucessRequest("/all", AdapterDescriptionList.class);
//
//        assertEquals("http://streampipes.org/adapterlist", result.getUri());
//        assertEquals(1, result.getList().size());
//        assertEquals("http://t.id", result.getList().get(0).getUri());
//
//    }

    @Test
    public void getAllAdaptersFail() throws AdapterException {
        doThrow(new AdapterException(ERROR_MESSAGE)).when(adapterTemplateMasterManagement).getAllAdapterTemplates();
        adapterTemplateResource.setAdapterTemplateMasterManagement(adapterTemplateMasterManagement);

        getJsonLdFailRequest("/all");
    }

}
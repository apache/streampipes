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
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.DescriptionManagement;
import org.streampipes.connect.management.IDescriptionManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecriptionResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/master/description";
    }

    private Server server;

    private DescriptionResource descriptionResource;

    private IDescriptionManagement descriptionManagement;


    @Before
    public  void before() {
        Config.PORT = 8019;
        RestAssured.port = 8019;

        descriptionResource = new DescriptionResource();
        server = getServer(descriptionResource);
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
    public void getFormatsEmptySuccess() {
        mockDescriptionManagerFormats(new FormatDescriptionList());

        FormatDescriptionList resultObject = getJsonLdSucessRequest("/formats", FormatDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#2");
        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    @Test
    public void getFormatsSuccess() {
        List<FormatDescription> list = Arrays.asList(
                new FormatDescription("http://id/1", "name1", ""),
                new FormatDescription("http://id/2", "name2", ""));
        mockDescriptionManagerFormats(new FormatDescriptionList(list));

        FormatDescriptionList resultObject = getJsonLdSucessRequest("/formats", FormatDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#2");
        assertNotNull(resultObject.getList());
        assertEquals(2, resultObject.getList().size());
        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
        assertEquals("name1", resultObject.getList().get(0).getName());
        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
        assertEquals("name2", resultObject.getList().get(1).getName());
    }

    @Test
    public void getProtocolsEmptySuccess() {
        mockDescriptionManagerProtocols(new ProtocolDescriptionList());

        ProtocolDescriptionList resultObject = getJsonLdSucessRequest("/protocols", ProtocolDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#1");
        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    @Test
    public void getProtocolsSuccess() {
        List<ProtocolDescription> list = Arrays.asList(
                new ProtocolDescription("http://id/1", "name1", ""),
                new ProtocolDescription("http://id/2", "name2", ""));
        mockDescriptionManagerProtocols(new ProtocolDescriptionList(list));

        ProtocolDescriptionList resultObject = getJsonLdSucessRequest("/protocols", ProtocolDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#1");
        assertNotNull(resultObject.getList());
        assertEquals(2, resultObject.getList().size());
        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
        assertEquals("name1", resultObject.getList().get(0).getName());
        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
        assertEquals("name2", resultObject.getList().get(1).getName());
    }

    @Test
    public void getAdaptersEmptySucess() {
        mockDescriptionManagerAdapters(new AdapterDescriptionList());

        AdapterDescriptionList resultObject = getJsonLdSucessRequest("/adapters", AdapterDescriptionList.class);

        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    @Test
    public void getAdaptersSucess() {
        List<AdapterDescription> list = Arrays.asList(
                new AdapterDescription("http://id/1", "name1", ""),
                new AdapterDescription("http://id/2", "name2", ""));
        mockDescriptionManagerAdapters(new AdapterDescriptionList(list));

        AdapterDescriptionList resultObject = getJsonLdSucessRequest("/adapters", AdapterDescriptionList.class);

//        assertEquals(resultObject.getUri(), "http://bla.de#2");
        assertNotNull(resultObject.getList());
        assertEquals(2, resultObject.getList().size());
        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
        assertEquals("name1", resultObject.getList().get(0).getName());
        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
        assertEquals("name2", resultObject.getList().get(1).getName());
    }

    private void mockDescriptionManagerFormats(FormatDescriptionList formatDescriptionList){
        IDescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getFormats()).thenReturn(formatDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }

    private void mockDescriptionManagerProtocols(ProtocolDescriptionList protocolDescriptionList){
        IDescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getProtocols()).thenReturn(protocolDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }

    private void mockDescriptionManagerAdapters(AdapterDescriptionList adapterDescriptionList){
        IDescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getAdapters()).thenReturn(adapterDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }
}
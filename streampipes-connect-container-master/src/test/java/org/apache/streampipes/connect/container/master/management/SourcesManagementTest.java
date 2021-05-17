/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.container.master.management;

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ WorkerRestClient.class })
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class SourcesManagementTest {
    private final static String ID = "id_1234";

    @Before
    public  void before() {
        PowerMockito.mockStatic(WorkerRestClient.class);
    }

    @Ignore
    @Test
    public void addAdapterSuccess() throws Exception {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());
        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);
        doNothing().when(WorkerRestClient.class, "invokeSetAdapter", anyString(), any());

        sourcesManagement.addAdapter(ID, new SpDataSet(), "user");

        verify(adapterStorage, times(1)).getAllAdapters();
        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.invokeSetAdapter(eq("/"), any());

    }

    @Ignore
    @Test(expected = AdapterException.class)
    public void addAdapterFail() throws Exception {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());
        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);

        org.powermock.api.mockito.PowerMockito.doThrow(new AdapterException()).when(WorkerRestClient.class, "stopSetAdapter", anyString(), any());

        sourcesManagement.detachAdapter( ID, "id1", "user");
    }

    @Ignore
    @Test
    public void detachAdapterSuccess() throws Exception {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());
        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);
        doNothing().when(WorkerRestClient.class, "stopSetAdapter", anyString(), any());

        sourcesManagement.detachAdapter(ID, "id1", "user");

        verify(adapterStorage, times(1)).getAllAdapters();
        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.stopSetAdapter(eq("/"), any());
    }

    @Ignore
    @Test(expected = AdapterException.class)
    public void detachAdapterFail() throws Exception {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());
        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);
        org.powermock.api.mockito.PowerMockito.doThrow(new AdapterException()).when(WorkerRestClient.class, "stopSetAdapter", anyString(), any());

        sourcesManagement.detachAdapter( ID, "id1", "user");
    }

    @Test
    public void getAllAdaptersInstallDescriptionSuccess() throws Exception {

        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());

        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);
        sourcesManagement.setConnectHost("host");

        String result = sourcesManagement.getAllAdaptersInstallDescription("user@fzi.de");
        assertEquals(getJsonString(), result);

    }

    @Test(expected = AdapterException.class)
    public void getAllAdaptersInstallDescriptionFail() throws Exception {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        AdapterDescription adapterDescription = new GenericAdapterSetDescription();
        adapterDescription.setUri(" ");
        when(adapterStorage.getAllAdapters()).thenReturn(Arrays.asList(adapterDescription));
        SourcesManagement sourcesManagement = new SourcesManagement(adapterStorage);
        sourcesManagement.setConnectHost("host");

        sourcesManagement.getAllAdaptersInstallDescription(" ");

    }

    private List<AdapterDescription> getAdapterDescriptionList() {
        GenericAdapterSetDescription adapterSetDescription = new GenericAdapterSetDescription();

        adapterSetDescription.setUri(ID);
        adapterSetDescription.setId(ID);


        return Arrays.asList(adapterSetDescription);
    }


    private String getJsonString() {
        return "[" +
                "{" +
                "\"uri\":\"id_1234\"," +
                "\"name\":\"Adapter Stream\"," +
                "\"description\":\"This stream is generated by an StreamPipes Connect adapter. ID of adapter: id_1234\"," +
                "\"type\":\"source\"," +
                "\"editable\":true," +
                "\"streams\":[" +
                "{" +
                "\"uri\":\"id_1234\"," +
                "\"name\":\"GenericAdapterSetDescription\"," +
                "\"description\":\"\"," +
                "\"type\":\"set\"," +
                "\"editable\":false" +
                "}" +
                "]" +
                "}" +
                "]";
    }
}

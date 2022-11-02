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
package org.apache.streampipes.connect.container.master.health;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdapterHealthCheckTest {

    private final String testElementId = "testElementId";

    @Test
    public void getAllRunningInstancesAdapterDescriptions() {
        AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());

        AdapterHealthCheck adapterHealthCheck = new AdapterHealthCheck(adapterStorage, null);
        Map<String, AdapterDescription> result = adapterHealthCheck.getAllRunningInstancesAdapterDescriptions();

        assertNotNull(result);
        assertEquals(1, result.keySet().size());
        assertEquals(getAdapterDescriptionList().get(0), result.get(testElementId));
    }

    @Test
    public void getAllWorkersWithAdapters() {
        AdapterInstanceStorageImpl adapterStorage = mock(AdapterInstanceStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(getAdapterDescriptionList());

        AdapterHealthCheck adapterHealthCheck = new AdapterHealthCheck(null, null);
        Map<String, List<AdapterDescription>> result = adapterHealthCheck.getAllWorkersWithAdapters(getAdapterDescriptionMap());

        assertNotNull(result);
        assertEquals(1, result.keySet().size());
        String selectedEndpointUrl = "http://test.de";
        assertEquals(1, result.get(selectedEndpointUrl).size());
        assertEquals(getAdapterDescriptionList().get(0), result.get(selectedEndpointUrl).get(0));
    }

    @Test
    public void recoverRunningAdaptersTest() throws AdapterException {
        AdapterMasterManagement adapterMasterManagementMock = mock(AdapterMasterManagement.class);
        AdapterHealthCheck adapterHealthCheck = new AdapterHealthCheck(null, adapterMasterManagementMock);

        adapterHealthCheck.recoverAdapters(getAdaptersToRecoverData(true));

        verify(adapterMasterManagementMock, times(1)).startStreamAdapter(any());
    }


    @Test
    public void recoverStoppedAdaptersTest() throws AdapterException {
        AdapterMasterManagement adapterMasterManagementMock = mock(AdapterMasterManagement.class);
        AdapterHealthCheck adapterHealthCheck = new AdapterHealthCheck(null, adapterMasterManagementMock);

        adapterHealthCheck.recoverAdapters(getAdaptersToRecoverData(false));

        verify(adapterMasterManagementMock, times(0)).startStreamAdapter(any());
    }

    private Map<String, AdapterDescription> getAdaptersToRecoverData(boolean isRunning) {
        Map<String, AdapterDescription> adaptersToRecover = new HashMap<>();
        AdapterStreamDescription ad = SpecificDataStreamAdapterBuilder.create("").build();
        ad.setRunning(isRunning);
        adaptersToRecover.put("", ad);
        return adaptersToRecover;
    }

    private List<AdapterDescription> getAdapterDescriptionList() {

        SpecificAdapterStreamDescription adapterStreamDescription = SpecificDataStreamAdapterBuilder
                .create("testAppId", "Test Adapter", "")
                .elementId(testElementId)
                .build();
        adapterStreamDescription.setSelectedEndpointUrl("http://test.de");

        return List.of(adapterStreamDescription);
    }

    private Map<String, AdapterDescription> getAdapterDescriptionMap() {
        Map<String, AdapterDescription> result = new HashMap<>();
        result.put(testElementId, getAdapterDescriptionList().get(0));

       return result;
    }

}
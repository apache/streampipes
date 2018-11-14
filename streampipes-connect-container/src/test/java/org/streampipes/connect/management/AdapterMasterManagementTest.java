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

package org.streampipes.connect.management;

import org.junit.Test;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterMasterManagementTest {
    @Test
    public void getAdapterSuccess() throws AdapterException {
        String id = GenericAdapterStreamDescription.ID;
        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        AdapterDescription result = adapterMasterManagement.getAdapter(id, adapterStorage);

        assertEquals(id, result.getUri());
    }

    @Test(expected = AdapterException.class)
    public void getAdapterFailNull() throws AdapterException {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(null);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAdapter("id2", adapterStorage);
    }

    @Test(expected = AdapterException.class)
    public void getAdapterFail() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

        String id = "http://t.id";
        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAdapter("id2", adapterStorage);
    }

    @Test
    public void getAllAdaptersSuccess() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        List<AdapterDescription> result = adapterMasterManagement.getAllAdapters(adapterStorage);

        assertEquals(1, result.size());
        assertEquals(GenericAdapterStreamDescription.ID, result.get(0).getUri());
    }

    @Test(expected = AdapterException.class)
    public void getAllAdaptersFail() throws AdapterException {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(null);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAllAdapters(adapterStorage);

    }
}
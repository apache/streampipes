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

package org.streampipes.connect.management.master;

import org.junit.Test;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.storage.couchdb.impl.AdapterTemplateStorageImpl;

import java.util.Arrays;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class AdapterTemplateMasterManagementTest {

    @Test
    public void getAllAdapterTemplates() throws AdapterException {
        String uri = "http://test.uri";
        AdapterDescription adapterDescription = new GenericAdapterStreamDescription();
        adapterDescription.setUri(uri);

        AdapterTemplateStorageImpl adapterStorage = mock(AdapterTemplateStorageImpl.class);
        when(adapterStorage.getAllAdapterTemplates()).thenReturn(Arrays.asList(adapterDescription));

        AdapterTemplateMasterManagement adapterTemplateMasterManagement = new AdapterTemplateMasterManagement();
        adapterTemplateMasterManagement.setAdapterTemplateStorage(adapterStorage);


        AdapterDescriptionList result = adapterTemplateMasterManagement.getAllAdapterTemplates();


        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        assertEquals(uri, result.getList().get(0).getUri());
    }
}
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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.format.json.arraykey.JsonFormat;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.GenericDataSetAdapter;
import org.streampipes.connect.management.master.DescriptionManagement;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.grounding.FormatDescriptionList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
public class DescriptionManagementTest {


    @Test
    public void getFormats() {
        Map<String, Format> allFormats = new HashMap<>();
        allFormats.put(JsonFormat.ID, new JsonFormat());

        PowerMockito.mockStatic(AdapterRegistry.class);
        Mockito.when(AdapterRegistry.getAllFormats())
                .thenReturn(allFormats);

        DescriptionManagement descriptionManagement = new DescriptionManagement();

        FormatDescriptionList result = descriptionManagement.getFormats();

        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        assertEquals(JsonFormat.ID, result.getList().get(0).getUri());
    }

    @Test
    public void getAdapters() {
        Map<String, Adapter> allAdapters = new HashMap<>();
        allAdapters.put(GenericDataSetAdapter.ID, new GenericDataSetAdapter());

        PowerMockito.mockStatic(AdapterRegistry.class);
        Mockito.when(AdapterRegistry.getAllAdapters())
                .thenReturn(allAdapters);

        DescriptionManagement descriptionManagement = new DescriptionManagement();

        AdapterDescriptionList result = descriptionManagement.getAdapters();

        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
        assertEquals(GenericDataSetAdapter.ID, result.getList().get(0).getAdapterId());
    }
}
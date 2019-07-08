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

package org.streampipes.connect.adapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.adapter.model.generic.GenericDataSetAdapter;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;


import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
public class AdapterRegistryTest {

    @Test
    public void getAdapterSuccess() {
        AdapterDescription adapterDescription = new GenericAdapterSetDescription();
        adapterDescription.setAdapterId(GenericDataSetAdapter.ID);
        Adapter adapter = AdapterRegistry.getAdapter(adapterDescription);

        assertEquals(adapter.getId(), GenericDataSetAdapter.ID);
    }

    @Test
    public void getAdapterFailAdapterDescriptionNull() {
        Adapter adapter = AdapterRegistry.getAdapter(null);
        assertNull(adapter);
    }
}
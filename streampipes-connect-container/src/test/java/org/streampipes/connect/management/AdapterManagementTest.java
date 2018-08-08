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
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AdapterManagementTest {


    @Test
    public void stopStreamAdapterFail() {
        String expected = "Adapter with id http://test.de was not found in this container and cannot be stopped.";
        AdapterStreamDescription asd = new AdapterStreamDescription();
        asd.setUri("http://test.de");

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

//        String result = adapterManagement.stopStreamAdapter(asd);
//        assertEquals(expected, result);
    }

    @Test
    public void stopStreamAdapterSuccess() {
        String id = "http://test.de";
        AdapterStreamDescription asd = new AdapterStreamDescription();
        asd.setUri(id);

        Adapter adapter = mock(Adapter.class);

        RunningAdapterInstances.INSTANCE.addAdapter(id, adapter);

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

//        String result = adapterManagement.stopStreamAdapter(asd);
//        assertEquals("", result);
    }

    @Test
    public void stopSetAdapterFail() {
        String expected = "Adapter with id http://test.de was not found in this container and cannot be stopped.";
        AdapterSetDescription asd = new AdapterSetDescription();
        asd.setUri("http://test.de");

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

//        String result = adapterManagement.stopSetAdapter(asd);
//        assertEquals(expected, result);
    }

    @Test
    public void stopSetAdapterSuccess() {
        String id = "http://test.de";
        AdapterSetDescription asd = new AdapterSetDescription();
        asd.setUri(id);

        Adapter adapter = mock(Adapter.class);

        RunningAdapterInstances.INSTANCE.addAdapter(id, adapter);

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

//        String result = adapterManagement.stopSetAdapter(asd);
//        assertEquals("", result);
    }
}
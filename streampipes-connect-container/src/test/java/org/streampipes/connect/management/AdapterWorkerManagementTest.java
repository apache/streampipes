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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.specific.SpecificDataSetAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.worker.AdapterWorkerManagement;
import org.streampipes.connect.utils.Utils;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.streampipes.model.connect.guess.GuessSchema;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
public class AdapterWorkerManagementTest {

    @Test
    public void startStreamAdapterSucess() throws AdapterException {
        TestAdapter testAdapter = new TestAdapter();

        PowerMockito.mockStatic(AdapterRegistry.class);
        Mockito.when(AdapterRegistry.getAdapter(any(AdapterDescription.class)))
                .thenReturn(testAdapter);
        AdapterWorkerManagement adapterWorkerManagement = new AdapterWorkerManagement();

        adapterWorkerManagement.invokeStreamAdapter(Utils.getMinimalStreamAdapter());

        assertTrue(testAdapter.calledStart);
        assertNotNull(RunningAdapterInstances.INSTANCE.removeAdapter("http://t.de/"));
    }

    @Test
    public void stopStreamAdapterFail() {
        String expected = "Adapter with id http://test.de was not found in this container and cannot be stopped.";
        AdapterStreamDescription asd = new GenericAdapterStreamDescription();
        asd.setUri("http://test.de");

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

        try {
            adapterManagement.stopStreamAdapter(asd);
            fail();
        } catch (AdapterException e) {
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    public void stopStreamAdapterSuccess() throws AdapterException {
        TestAdapter testAdapter = new TestAdapter();
        RunningAdapterInstances.INSTANCE.addAdapter("http://t.de/", testAdapter);
        AdapterWorkerManagement adapterWorkerManagement = new AdapterWorkerManagement();
        adapterWorkerManagement.stopStreamAdapter(Utils.getMinimalStreamAdapter());

        assertTrue(testAdapter.calledStop);

    }

    @Test
    public void stopSetAdapterFail() {
        String expected = "Adapter with id http://test.de was not found in this container and cannot be stopped.";
        AdapterSetDescription asd = new GenericAdapterSetDescription();
        asd.setUri("http://test.de");

        AdapterWorkerManagement adapterManagement = new AdapterWorkerManagement();

        try {
            adapterManagement.stopSetAdapter(asd);
            fail();
        } catch (AdapterException e) {
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    public void stopSetAdapterSuccess() throws AdapterException {
        TestAdapter testAdapter = new TestAdapter();
        RunningAdapterInstances.INSTANCE.addAdapter("http://t.de/", testAdapter);
        AdapterWorkerManagement adapterWorkerManagement = new AdapterWorkerManagement();
        adapterWorkerManagement.stopSetAdapter(Utils.getMinimalSetAdapter());

        assertTrue(testAdapter.calledStop);
    }

    private class TestAdapter extends SpecificDataSetAdapter {

        public TestAdapter() {
            super(null);
        }

        public boolean calledStart = false;
        public boolean calledStop = false;



        @Override
        public SpecificAdapterSetDescription declareModel() {
            return null;
        }

        @Override
        public void startAdapter() throws AdapterException {
            calledStart = true;
        }

        @Override
        public void stopAdapter() throws AdapterException {
            calledStop = true;
        }

        @Override
        public Adapter getInstance(AdapterDescription adapterDescription) {
            return null;
        }

        @Override
        public GuessSchema getSchema(AdapterDescription adapterDescription) {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }
    }
}
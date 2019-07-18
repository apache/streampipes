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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.streampipes.connect.management.worker.AdapterWorkerManagement;
import org.streampipes.connect.utils.Utils;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.model.connect.guess.GuessSchema;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
public class AdapterWorkerManagementTest {

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
        TestAdapter testAdapter = getTestAdapterInstance();
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
        TestAdapter testAdapter = getTestAdapterInstance();

        RunningAdapterInstances.INSTANCE.addAdapter("http://t.de/", testAdapter);
        AdapterWorkerManagement adapterWorkerManagement = new AdapterWorkerManagement();
        adapterWorkerManagement.stopSetAdapter(Utils.getMinimalSetAdapter());

        assertTrue(testAdapter.calledStop);
    }

    private TestAdapter getTestAdapterInstance() {
        SpecificAdapterSetDescription description = new SpecificAdapterSetDescription();
        description.setRules(new ArrayList<>());
        TestAdapter testAdapter = new TestAdapter(description);

        return testAdapter;
    }

    private class TestAdapter extends SpecificDataSetAdapter {


        public TestAdapter(SpecificAdapterSetDescription description) {
            super(description);
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
        public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
            return null;
        }

        @Override
        public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription) {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

    }
}
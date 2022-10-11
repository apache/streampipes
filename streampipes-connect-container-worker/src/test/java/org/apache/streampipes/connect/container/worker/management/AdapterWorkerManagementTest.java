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

package org.apache.streampipes.connect.container.worker.management;

import org.apache.streampipes.connect.RunningAdapterInstances;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.AdapterRegistry;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.worker.utils.Utils;
import org.apache.streampipes.model.connect.adapter.*;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class AdapterWorkerManagementTest {

    @Test
    public void stopStreamAdapterSuccess() throws AdapterException {
        TestAdapter testAdapter = getTestAdapterInstance();
        RunningAdapterInstances.INSTANCE.addAdapter("http://t.de/", testAdapter, null);
        AdapterWorkerManagement adapterWorkerManagement = new AdapterWorkerManagement();
        adapterWorkerManagement.stopStreamAdapter(Utils.getMinimalStreamAdapter());

        assertTrue(testAdapter.calledStop);

    }

    @Test
    public void stopSetAdapterSuccess() throws AdapterException {
        TestAdapter testAdapter = getTestAdapterInstance();

        RunningAdapterInstances.INSTANCE.addAdapter("http://t.de/", testAdapter, null);
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

        public boolean calledStart = false;
        public boolean calledStop = false;

        public TestAdapter(SpecificAdapterSetDescription description) {
            super(description);
        }

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

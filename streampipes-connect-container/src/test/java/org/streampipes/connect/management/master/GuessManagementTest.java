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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdapterRegistry.class })
public class GuessManagementTest {

    @Before
    public  void before() {
        PowerMockito.mockStatic(AdapterRegistry.class);
    }


    @Test
    public void guessSchema() throws Exception {
        when(AdapterRegistry.class, "getAdapter", any()).thenReturn(new TestAdapter());

        GuessManagement guessManagement = new GuessManagement();

        GuessSchema result = guessManagement.guessSchema(null);

        assertNotNull(result);
        assertNotNull(result.eventSchema);
        assertNotNull(result.eventSchema.getEventProperties());
        assertEquals(1, result.eventSchema.getEventProperties().size());
        assertEquals("id", result.eventSchema.getEventProperties().get(0).getRuntimeName());
    }

    private class TestAdapter extends Adapter {

        @Override
        public GuessSchema getSchema(AdapterDescription adapterDescription) {

            EventSchema eventSchema = new EventSchema();
            EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
            eventPropertyPrimitive.setRuntimeType("http://schema.org/Number");
            eventPropertyPrimitive.setRuntimeName("id");

            eventSchema.setEventProperties(Arrays.asList(eventPropertyPrimitive));
            GuessSchema guessSchema = new GuessSchema();
            guessSchema.setEventSchema(eventSchema);
            return guessSchema;
        }

        @Override
        public AdapterDescription declareModel() {
            return null;
        }

        @Override
        public void startAdapter() throws AdapterException {

        }

        @Override
        public void stopAdapter() throws AdapterException {

        }

        @Override
        public Adapter getInstance(AdapterDescription adapterDescription) {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

    }
}
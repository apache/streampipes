/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
package org.streampipes.connect.adapters.generic;

import org.junit.Test;
import org.streampipes.connect.adapter.preprocessing.Util;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.schema.EventSchema;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UtilTest {


    @Test
    public void getEventSchemaStreamDescription() {
        GenericAdapterStreamDescription adapter = new GenericAdapterStreamDescription();
        adapter.setDataStream(new SpDataStream());
        adapter.getDataStream().setEventSchema(new EventSchema());

        assertTrue(Util.getEventSchema((GenericAdapterDescription) adapter) instanceof EventSchema);
    }

    @Test
    public void getEventSchemaNullStreamDescription() {
        GenericAdapterStreamDescription adapter = new GenericAdapterStreamDescription();
        adapter.setDataStream(new SpDataStream());

        assertNull(Util.getEventSchema((GenericAdapterDescription) adapter));
    }

    @Test
    public void getEventSchemaSetDescription() {
        GenericAdapterSetDescription adapter = new GenericAdapterSetDescription();
        adapter.setDataSet(new SpDataSet());
        adapter.getDataSet().setEventSchema(new EventSchema());

        assertTrue(Util.getEventSchema((GenericAdapterDescription) adapter) instanceof EventSchema);
    }

    @Test
    public void getEventSchemaNullSetDescription() {
        GenericAdapterSetDescription adapter = new GenericAdapterSetDescription();
        adapter.setDataSet(new SpDataSet());

        assertNull(Util.getEventSchema((GenericAdapterDescription) adapter));
    }
}
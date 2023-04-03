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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SourcesManagementTest {

  @Before
  public void before() {
  }

  @Test
  public void updateDataStream() {
    String newAdapterName = "newName";
    EventSchema newEventSchema = new EventSchema();
    newEventSchema.addEventProperty(new EventPropertyPrimitive());

    AdapterDescription adapterDescription = new AdapterDescription();
    SpDataStream newDataStream = new SpDataStream();
    newDataStream.setEventSchema(newEventSchema);
    adapterDescription.setName(newAdapterName);
    adapterDescription.setDataStream(newDataStream);

    SpDataStream result = SourcesManagement.updateDataStream(adapterDescription, new SpDataStream());

    assertNotNull(result);
    assertEquals(newAdapterName, result.getName());
    assertEquals(newEventSchema, result.getEventSchema());
  }
}

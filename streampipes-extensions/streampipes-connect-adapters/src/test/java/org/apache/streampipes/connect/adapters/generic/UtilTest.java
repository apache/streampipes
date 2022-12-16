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
package org.apache.streampipes.connect.adapters.generic;

import org.apache.streampipes.connect.adapter.preprocessing.Util;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.Test;

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
  public void getEventSchemaSetDescription() {
    GenericAdapterSetDescription adapter = new GenericAdapterSetDescription();
    adapter.setDataSet(new SpDataSet());
    adapter.getDataSet().setEventSchema(new EventSchema());

    assertTrue(Util.getEventSchema((GenericAdapterDescription) adapter) instanceof EventSchema);
  }

}
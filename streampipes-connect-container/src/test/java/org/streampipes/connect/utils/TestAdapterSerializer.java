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
package org.streampipes.connect.utils;

import org.junit.Test;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestAdapterSerializer {

  @Test
  public void testGenericAdapterStreamSerialization() {
    GenericAdapterStreamDescription desc = new GenericAdapterStreamDescription();
    FormatDescription formatDescription = new FormatDescription();
    formatDescription.addConfig(new FreeTextStaticProperty("internal-id", "test", "test"));
    desc.setFormatDescription(formatDescription);

    String serialized = (GsonSerializer.getAdapterGson().toJson(desc));

    AdapterDescription deserialized = GsonSerializer.getAdapterGson().fromJson(serialized, AdapterDescription.class);
    assertEquals(deserialized.getAdapterId(), "http://streampipes.org/genericadapterstreamdescription");
  }

  @Test
  public void testEventPropertySerialization() {
    EventSchema schema = new EventSchema();
    EventProperty primitive = new EventPropertyPrimitive();
    primitive.setRuntimeName("test");
    ((EventPropertyPrimitive) primitive).setRuntimeType("test");
    schema.setEventProperties(Arrays.asList(primitive));
    String serialized = GsonSerializer.getGson().toJson(primitive);
    System.out.println(serialized);
  }

//  @Test
//  public void testCouchDbSerialization() {
//    GenericAdapterStreamDescription desc = new GenericAdapterStreamDescription();
//
//    new AdapterStorageImpl().storeAdapter(desc);
//
//    AdapterDescription deserialized = new AdapterStorageImpl().getAllAdapters().get(0);
//    System.out.println(deserialized.getAdapterId());
//  }
  
}

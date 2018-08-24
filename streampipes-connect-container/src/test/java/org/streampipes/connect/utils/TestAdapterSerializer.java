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
import org.streampipes.serializers.json.GsonSerializer;

import static org.junit.Assert.assertEquals;

public class TestAdapterSerializer {

  @Test
  public void testGenericAdapterStreamSerialization() {
    GenericAdapterStreamDescription desc = new GenericAdapterStreamDescription();

    String serialized = (GsonSerializer.getGsonBuilder().create().toJson(desc, AdapterDescription.class));

    AdapterDescription deserialized = GsonSerializer.getGson().fromJson(serialized, AdapterDescription.class);
    assertEquals(deserialized.getAdapterId(), "http://streampipes.org/genericadapterstreamdescription");
  }
  
}

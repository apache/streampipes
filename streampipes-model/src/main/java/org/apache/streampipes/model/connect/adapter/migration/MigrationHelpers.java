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

package org.apache.streampipes.model.connect.adapter.migration;

import org.apache.streampipes.model.connect.adapter.migration.utils.AdapterModels;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class MigrationHelpers {

  public static final String ID = "_id";
  public static final String REV = "_rev";

  public static final String APP_ID = "appId";

  public static final String PROPERTIES = "properties";

  public String getDocId(JsonObject adapter) {
    return adapter.get(ID).getAsString();
  }

  public String getRev(JsonObject adapter) {
    return adapter.get(REV).getAsString();
  }

  public void updateType(JsonObject adapter,
                         String typeFieldName) {
    adapter.add(typeFieldName, new JsonPrimitive(AdapterModels.NEW_MODEL));
  }

  public void updateFieldType(JsonObject adapter) {
    adapter.add("field_type", new JsonPrimitive(AdapterModels.NEW_MODEL));
  }

  public String getAdapterName(JsonObject adapter) {
    return adapter.get("properties").getAsJsonObject().get("name").getAsString();
  }
}

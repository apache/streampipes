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

package org.apache.streampipes.service.core.migrations.v093.format;

import org.apache.streampipes.service.core.migrations.v093.migrator.MigrationHelpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static org.apache.streampipes.service.core.migrations.v093.utils.DocumentKeys.ALTERNATIVES;
import static org.apache.streampipes.service.core.migrations.v093.utils.DocumentKeys.INTERNAL_NAME;

public class JsonFormatMigrator implements FormatMigrator {

  private String jsonFormatId;

  public JsonFormatMigrator(String jsonFormatId,
                            JsonObject existingFormat) {
    this.jsonFormatId = jsonFormatId;
  }

  @Override
  public void migrate(JsonObject newFormatProperties) {
    newFormatProperties.get(MigrationHelpers.PROPERTIES).getAsJsonObject()
        .get("staticProperties").getAsJsonArray()
        .get(0).getAsJsonObject()
        .get(MigrationHelpers.PROPERTIES).getAsJsonObject()
        .get(ALTERNATIVES).getAsJsonArray()
        .forEach(al -> {
          if (al.getAsJsonObject().get(INTERNAL_NAME).getAsString().equals(jsonFormatId)) {
            al.getAsJsonObject().add("selected", new JsonPrimitive(true));
          }
        });
  }
}

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

package org.apache.streampipes.model.connect.adapter.migration.format;

import org.apache.streampipes.model.connect.adapter.migration.MigrationHelpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static org.apache.streampipes.model.connect.adapter.migration.utils.DocumentKeys.ALTERNATIVES;
import static org.apache.streampipes.model.connect.adapter.migration.utils.DocumentKeys.INTERNAL_NAME;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_ARRAY_KEY_NEW_KEY;

public class JsonFormatMigrator implements FormatMigrator {

  private final String jsonFormatId;

  private final JsonObject existingFormat;

  public JsonFormatMigrator(String jsonFormatId,
                            JsonObject existingFormat) {
    this.jsonFormatId = jsonFormatId;
    this.existingFormat = existingFormat;
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

            //  If the type is JSON_ARRAY set the value for the key in the configuration
            if (this.jsonFormatId.equals(JSON_ARRAY_KEY_NEW_KEY)) {
              var keyValue = this.existingFormat.getAsJsonObject()
                  .get("config").getAsJsonArray()
                  .get(0).getAsJsonObject()
                  .get("properties").getAsJsonObject()
                  .get("value").getAsString();
              al.getAsJsonObject()
                  .get("staticProperty").getAsJsonObject()
                  .get("properties").getAsJsonObject()
                  .get("staticProperties").getAsJsonArray()
                  .get(0).getAsJsonObject()
                  .get("properties").getAsJsonObject()
                  .addProperty("value", keyValue);
            }
          }
        });
  }
}

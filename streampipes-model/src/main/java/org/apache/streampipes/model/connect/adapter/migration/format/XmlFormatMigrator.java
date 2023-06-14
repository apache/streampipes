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

import com.google.gson.JsonObject;

public class XmlFormatMigrator implements FormatMigrator {
  private final JsonObject formatDescription;

  public XmlFormatMigrator(JsonObject formatDescription) {
    this.formatDescription = formatDescription;
  }

  @Override
  public void migrate(JsonObject newFormatProperties) {
    var tagValue = this.formatDescription.getAsJsonObject()
        .get("config").getAsJsonArray()
        .get(0).getAsJsonObject()
        .get("properties").getAsJsonObject()
        .get("value").getAsString();
    newFormatProperties
        .getAsJsonObject("properties")
        .get("staticProperties")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("properties")
        .getAsJsonObject()
        .addProperty("value", tagValue);


  }
}

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

package org.apache.streampipes.service.core.migrations.v093.migrator;

import org.apache.streampipes.service.core.migrations.v093.format.CsvFormatMigrator;
import org.apache.streampipes.service.core.migrations.v093.format.EmptyFormatMigrator;
import org.apache.streampipes.service.core.migrations.v093.format.JsonFormatMigrator;
import org.apache.streampipes.service.core.migrations.v093.format.XmlFormatMigrator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.streampipes.service.core.migrations.v093.migrator.MigrationHelpers.PROPERTIES;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.CSV;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.CSV_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.GEOJSON_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.IMAGE;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.IMAGE_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.JSON;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.JSON_ARRAY_KEY_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.JSON_ARRAY_NO_KEY_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.JSON_OBJECT_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.JSON_OBJECT_NEW_KEY;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.XML;
import static org.apache.streampipes.service.core.migrations.v093.utils.FormatIds.XML_FORMAT_ID;
import static org.apache.streampipes.service.core.migrations.v093.utils.GenericAdapterUtils.applyFormat;
import static org.apache.streampipes.service.core.migrations.v093.utils.GenericAdapterUtils.getFormatTemplate;

public class GenericAdapterMigrator implements AdapterMigrator {

  private static final Logger LOG = LoggerFactory.getLogger(GenericAdapterMigrator.class);

  private static final String PROTOCOL_DESC_KEY = "protocolDescription";
  private static final String FORMAT_DESC_KEY = "formatDescription";
  private static final String CONFIG_KEY = "config";

  private final MigrationHelpers helpers;

  public GenericAdapterMigrator(MigrationHelpers helpers) {
    this.helpers = helpers;
  }

  @Override
  public void migrate(CouchDbClient couchDbClient, JsonObject adapter) {
    var adapterName = helpers.getAdapterName(adapter);
    helpers.updateType(adapter);
    helpers.updateFieldType(adapter);

    JsonObject formatDescription = adapter.get(PROPERTIES).getAsJsonObject().get(FORMAT_DESC_KEY).getAsJsonObject();
    JsonObject protocolDescription = adapter.get(PROPERTIES).getAsJsonObject().get(PROTOCOL_DESC_KEY).getAsJsonObject();

    migrateProtocolDescription(adapter, protocolDescription);
    migrateFormatDescription(adapter, formatDescription);

    adapter.get(PROPERTIES).getAsJsonObject().remove(FORMAT_DESC_KEY);
    adapter.get(PROPERTIES).getAsJsonObject().remove(PROTOCOL_DESC_KEY);

    couchDbClient.update(adapter);

    LOG.info("Successfully migrated adapter {}", adapterName);
  }

  private void migrateProtocolDescription(JsonObject adapter,
                                          JsonObject protocolDescription) {
    JsonArray config = adapter.get(PROPERTIES).getAsJsonObject().get(CONFIG_KEY).getAsJsonArray();
    JsonArray protocolDescriptionConfig = protocolDescription.get(CONFIG_KEY).getAsJsonArray();
    protocolDescriptionConfig.forEach(config::add);
  }

  private void migrateFormatDescription(JsonObject adapter,
                                        JsonObject formatDescription) {
    var adapterConfig = adapter
        .get(PROPERTIES)
        .getAsJsonObject()
        .get(CONFIG_KEY)
        .getAsJsonArray();

    var formatTemplate = getFormatTemplate();

    if (isFormat(formatDescription, JSON_OBJECT_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_OBJECT_NEW_KEY, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, JSON_ARRAY_KEY_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_ARRAY_KEY_FORMAT_ID, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, JSON_ARRAY_NO_KEY_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_ARRAY_NO_KEY_FORMAT_ID, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, CSV_FORMAT_ID)) {
      var migrator = new CsvFormatMigrator(CSV_FORMAT_ID, formatDescription);
      applyFormat(CSV, formatTemplate, migrator);
    } else if (isFormat(formatDescription, GEOJSON_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(GEOJSON_FORMAT_ID, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, XML_FORMAT_ID)) {
      var migrator = new XmlFormatMigrator(XML_FORMAT_ID, formatDescription);
      applyFormat(XML, formatTemplate, migrator);
    } else if (isFormat(formatDescription, IMAGE_FORMAT_ID)) {
      applyFormat(IMAGE, formatTemplate, new EmptyFormatMigrator());
    } else {
      LOG.warn("Found unknown format {}", getAppId(formatDescription));
    }

    adapterConfig.add(formatTemplate);
  }

  private boolean isFormat(JsonObject formatDescription,
                           String format) {
    return getAppId(formatDescription).equals(format);
  }

  private String getAppId(JsonObject formatDescription) {
    return formatDescription
        .getAsJsonObject()
        .get(MigrationHelpers.APP_ID).getAsString();
  }
}

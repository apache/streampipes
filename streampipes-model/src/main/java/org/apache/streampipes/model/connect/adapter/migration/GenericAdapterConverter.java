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

import org.apache.streampipes.model.connect.adapter.migration.format.CsvFormatMigrator;
import org.apache.streampipes.model.connect.adapter.migration.format.EmptyFormatMigrator;
import org.apache.streampipes.model.connect.adapter.migration.format.JsonFormatMigrator;
import org.apache.streampipes.model.connect.adapter.migration.format.XmlFormatMigrator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.streampipes.model.connect.adapter.migration.MigrationHelpers.PROPERTIES;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.CSV;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.CSV_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.GEOJSON_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.GEOJSON_NEW_KEY;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.IMAGE;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.IMAGE_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_ARRAY_KEY_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_ARRAY_KEY_NEW_KEY;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_ARRAY_NO_KEY_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_ARRAY_NO_KEY_NEW_KEY;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_OBJECT_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.JSON_OBJECT_NEW_KEY;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.XML;
import static org.apache.streampipes.model.connect.adapter.migration.utils.FormatIds.XML_FORMAT_ID;
import static org.apache.streampipes.model.connect.adapter.migration.utils.GenericAdapterUtils.applyFormat;
import static org.apache.streampipes.model.connect.adapter.migration.utils.GenericAdapterUtils.getFormatTemplate;

public class GenericAdapterConverter implements IAdapterConverter {

  private static final Logger LOG = LoggerFactory.getLogger(GenericAdapterConverter.class);
  private static final String PROTOCOL_DESC_KEY = "protocolDescription";
  private static final String FORMAT_DESC_KEY = "formatDescription";
  private static final String CONFIG_KEY = "config";

  private final MigrationHelpers helpers;

  private final String typeFieldName;
  private final boolean importMode;

  public GenericAdapterConverter(boolean importMode) {
    this.helpers = new MigrationHelpers();
    this.importMode = importMode;
    this.typeFieldName = importMode ? "@class" : "type";
  }

  @Override
  public JsonObject convert(JsonObject adapter) {
    helpers.updateType(adapter, typeFieldName);
    if (!importMode) {
      helpers.updateFieldType(adapter);
    }

    JsonObject formatDescription = getProperties(adapter).get(FORMAT_DESC_KEY).getAsJsonObject();
    JsonObject protocolDescription = getProperties(adapter).get(PROTOCOL_DESC_KEY).getAsJsonObject();

    migrateProtocolDescription(adapter, protocolDescription);
    migrateFormatDescription(adapter, formatDescription);

    getProperties(adapter).remove(FORMAT_DESC_KEY);
    getProperties(adapter).remove(PROTOCOL_DESC_KEY);

    return adapter;
  }

  private JsonObject getProperties(JsonObject object) {
    return importMode ? object : object.get(PROPERTIES).getAsJsonObject();
  }

  private void migrateProtocolDescription(JsonObject adapter,
                                          JsonObject protocolDescription) {
    JsonArray config = getProperties(adapter).get(CONFIG_KEY).getAsJsonArray();
    JsonArray protocolDescriptionConfig = protocolDescription.get(CONFIG_KEY).getAsJsonArray();
    protocolDescriptionConfig.forEach(config::add);
  }

  private void migrateFormatDescription(JsonObject adapter,
                                        JsonObject formatDescription) {
    var adapterConfig = getProperties(adapter)
        .get(CONFIG_KEY)
        .getAsJsonArray();

    var formatTemplate = getFormatTemplate();

    if (isFormat(formatDescription, JSON_OBJECT_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_OBJECT_NEW_KEY, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, JSON_ARRAY_KEY_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_ARRAY_KEY_NEW_KEY, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, JSON_ARRAY_NO_KEY_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(JSON_ARRAY_NO_KEY_NEW_KEY, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, CSV_FORMAT_ID)) {
      var migrator = new CsvFormatMigrator(formatDescription);
      applyFormat(CSV, formatTemplate, migrator);
    } else if (isFormat(formatDescription, GEOJSON_FORMAT_ID)) {
      var migrator = new JsonFormatMigrator(GEOJSON_NEW_KEY, formatDescription);
      applyFormat(JSON, formatTemplate, migrator);
    } else if (isFormat(formatDescription, XML_FORMAT_ID)) {
      var migrator = new XmlFormatMigrator(formatDescription);
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

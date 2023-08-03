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

package org.apache.streampipes.model.connect.adapter.migration.utils;

import org.apache.streampipes.model.connect.adapter.migration.MigrationHelpers;
import org.apache.streampipes.model.connect.adapter.migration.format.FormatMigrator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import static org.apache.streampipes.model.connect.adapter.migration.utils.DocumentKeys.INTERNAL_NAME;

public class GenericAdapterUtils {

  public static void applyFormat(String formatType,
                                 JsonObject formatConfig,
                                 FormatMigrator migrator) {

    formatConfig
        .get(MigrationHelpers.PROPERTIES).getAsJsonObject()
        .get(DocumentKeys.ALTERNATIVES).getAsJsonArray()
        .forEach(el -> {
          var alternative = el.getAsJsonObject();
          if (alternative.get(INTERNAL_NAME).getAsString().equals(formatType)) {
            alternative.add("selected", new JsonPrimitive(true));
            migrator.migrate(alternative.get("staticProperty").getAsJsonObject());
          }
        });
  }

  public static JsonObject getFormatTemplate() {
    return JsonParser.parseString("""
        {
                "type": "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives",
                "properties": {
                  "alternatives": [
                    {
                      "elementId": "sp:staticpropertyalternative:NhVXLH",
                      "selected": false,
                      "staticProperty": {
                        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup",
                        "properties": {
                          "staticProperties": [
                            {
        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives",
                              "properties": {
                                "alternatives": [
                                  {
                                    "elementId": "sp:staticpropertyalternative:XbWYtr",
                                    "selected": false,
                                    "valueRequired": false,
                                    "staticPropertyType": "StaticPropertyAlternative",
                                    "index": 0,
                                    "label": "Single Object",
                                    "description": "object",
                                    "internalName": "object",
                                    "predefined": false
                                  },
                                  {
                                    "elementId": "sp:staticpropertyalternative:yFmmae",
                                    "selected": false,
                                    "valueRequired": false,
                                    "staticPropertyType": "StaticPropertyAlternative",
                                    "index": 1,
                                    "label": "Array",
                                    "description": "array",
                                    "internalName": "array",
                                    "predefined": false
                                  },
                                  {
                                    "elementId": "sp:staticpropertyalternative:XDFNhI",
                                    "selected": false,
                                    "staticProperty": {
        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup",
                                      "properties": {
                                        "staticProperties": [
                                          {
        "type": "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty",
                                            "properties": {
                                              "requiredDatatype": "http://www.w3.org/2001/XMLSchema#string",
                                              "multiLine": false,
                                              "htmlAllowed": false,
                                              "htmlFontFormat": false,
                                              "placeholdersSupported": false,
                                              "valueRequired": false,
                                              "staticPropertyType": "FreeTextStaticProperty",
                                              "index": 0,
                                              "label": "Key",
                                              "description": "Key of the array within the Json object",
                                              "internalName": "key",
                                              "predefined": false
                                            }
                                          }
                                        ],
                                        "horizontalRendering": false,
                                        "valueRequired": false,
                                        "staticPropertyType": "StaticPropertyGroup",
                                        "index": 0,
                                        "label": "Delimiter",
                                        "description": "",
                                        "internalName": "arrayFieldConfig",
                                        "predefined": false
                                      }
                                    },
                                    "valueRequired": false,
                                    "staticPropertyType": "StaticPropertyAlternative",
                                    "index": 2,
                                    "label": "Array Field",
                                    "description": "arrayField",
                                    "internalName": "arrayField",
                                    "predefined": false
                                  },
                                  {
                                    "elementId": "sp:staticpropertyalternative:wVjlmK",
                                    "selected": false,
                                    "valueRequired": false,
                                    "staticPropertyType": "StaticPropertyAlternative",
                                    "index": 3,
                                    "label": "GeoJSON",
                                    "description": "geojson",
                                    "internalName": "geojson",
                                    "predefined": false
                                  }
                                ],
                                "valueRequired": false,
                                "staticPropertyType": "StaticPropertyAlternatives",
                                "index": 0,
                                "label": "",
                                "description": "",
                                "internalName": "json_options",
                                "predefined": false
                              }
                            }
                          ],
                          "horizontalRendering": false,
                          "valueRequired": false,
                          "staticPropertyType": "StaticPropertyGroup",
                          "index": 0,
                          "label": "Json",
                          "description": "",
        "internalName": "org.apache.streampipes.extensions.management.connect.adapter.parser.json",
                          "predefined": false
                        }
                      },
                      "valueRequired": false,
                      "staticPropertyType": "StaticPropertyAlternative",
                      "index": 0,
                      "label": "Json",
                      "description": "",
                      "internalName": "Json",
                      "predefined": false
                    },
                    {
                      "elementId": "sp:staticpropertyalternative:HzpRNt",
                      "selected": false,
                      "staticProperty": {
                        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup",
                        "properties": {
                          "staticProperties": [
                            {
                              "type": "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty",
                              "properties": {
                                "requiredDomainProperty": "http://www.w3.org/2001/XMLSchema#string",
                                "multiLine": false,
                                "htmlAllowed": false,
                                "htmlFontFormat": false,
                                "placeholdersSupported": false,
                                "valueRequired": false,
                                "staticPropertyType": "FreeTextStaticProperty",
                                "index": 0,
                                "label": "Delimiter",
                                "description": "The delimiter for json. Mostly either , or ;",
                                "internalName": "delimiter",
                                "predefined": false
                              }
                            },
                            {
                              "type": "org.apache.streampipes.model.staticproperty.AnyStaticProperty",
                              "properties": {
                                "options": [
                                  {
                                    "elementId": "sp:option:fAGcpO",
                                    "name": "Header",
                                    "selected": false,
                                    "internalName": "Header"
                                  }
                                ],
                                "horizontalRendering": false,
                                "valueRequired": false,
                                "staticPropertyType": "AnyStaticProperty",
                                "index": 1,
                                "label": "Header",
                                "description": "Does the CSV file include a header or not",
                                "internalName": "header",
                                "predefined": false
                              }
                            }
                          ],
                          "horizontalRendering": false,
                          "valueRequired": false,
                          "staticPropertyType": "StaticPropertyGroup",
                          "index": 0,
                          "label": "CSV",
                          "description": "Can be used to read CSV",
         "internalName": "org.apache.streampipes.extensions.management.connect.adapter.parser.csv",
                          "predefined": false
                        }
                      },
                      "valueRequired": false,
                      "staticPropertyType": "StaticPropertyAlternative",
                      "index": 1,
                      "label": "CSV",
                      "description": "Can be used to read CSV",
                      "internalName": "CSV",
                      "predefined": false
                    },
                    {
                      "elementId": "sp:staticpropertyalternative:eSsRuI",
                      "selected": false,
                      "staticProperty": {
                        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup",
                        "properties": {
                          "staticProperties": [
                            {
                              "type": "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty",
                              "properties": {
                                "requiredDomainProperty": "http://www.w3.org/2001/XMLSchema#string",
                                "multiLine": false,
                                "htmlAllowed": false,
                                "htmlFontFormat": false,
                                "placeholdersSupported": false,
                                "valueRequired": false,
                                "staticPropertyType": "FreeTextStaticProperty",
                                "index": 0,
                                "label": "Tag",
                                "description": "Information in the tag is transformed into an event",
                                "internalName": "tag",
                                "predefined": false
                              }
                            }
                          ],
                          "horizontalRendering": false,
                          "valueRequired": false,
                          "staticPropertyType": "StaticPropertyGroup",
                          "index": 0,
                          "label": "XML",
                          "description": "Can be used to read XML data",
        "internalName": "org.apache.streampipes.extensions.management.connect.adapter.parser.xml",
                          "predefined": false
                        }
                      },
                      "valueRequired": false,
                      "staticPropertyType": "StaticPropertyAlternative",
                      "index": 2,
                      "label": "XML",
                      "description": "Can be used to read XML data",
                      "internalName": "XML",
                      "predefined": false
                    },
                    {
                      "elementId": "sp:staticpropertyalternative:kjRhJe",
                      "selected": false,
                      "staticProperty": {
                        "type": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup",
                        "properties": {
                          "staticProperties": [],
                          "horizontalRendering": false,
                          "valueRequired": false,
                          "staticPropertyType": "StaticPropertyGroup",
                          "index": 0,
                          "label": "Image",
                          "description": "Processes images and transforms them into events",
        "internalName": "org.apache.streampipes.extensions.management.connect.adapter.parser.image",
                          "predefined": false
                        }
                      },
                      "valueRequired": false,
                      "staticPropertyType": "StaticPropertyAlternative",
                      "index": 3,
                      "label": "Image",
                      "description": "Processes images and transforms them into events",
                      "internalName": "Image",
                      "predefined": false
                    }
                  ],
                  "valueRequired": false,
                  "staticPropertyType": "StaticPropertyAlternatives",
                  "index": 0,
                  "label": "Format",
                  "description": "Select the format that is used to parse the events",
                  "internalName": "format",
                  "predefined": false
                }
              }""").getAsJsonObject();
  }
}

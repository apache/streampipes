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

package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.csv.CsvFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.csv.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.geojson.GeoJsonFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.geojson.GeoJsonParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.image.ImageFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.image.ImageParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.arraykey.JsonFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.arraykey.JsonParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.arraynokey.JsonArrayFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.arraynokey.JsonArrayParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.object.JsonObjectFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.object.JsonObjectParser;
import org.apache.streampipes.extensions.management.connect.adapter.format.xml.XmlFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.xml.XmlParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all implemented adapters
 */
public class AdapterRegistry {

  public static Map<String, IFormat> getAllFormats() {
    Map<String, IFormat> allFormats = new HashMap<>();

    allFormats.put(JsonFormat.ID, new JsonFormat());
    allFormats.put(JsonObjectFormat.ID, new JsonObjectFormat());
    allFormats.put(JsonArrayFormat.ID, new JsonArrayFormat());
    allFormats.put(CsvFormat.ID, new CsvFormat());
    allFormats.put(GeoJsonFormat.ID, new GeoJsonFormat());
    allFormats.put(XmlFormat.ID, new XmlFormat());
    allFormats.put(ImageFormat.ID, new ImageFormat());

    return allFormats;
  }

  public static Map<String, IParser> getAllParsers() {
    Map<String, IParser> allParsers = new HashMap<>();

    allParsers.put(JsonFormat.ID, new JsonParser());
    allParsers.put(JsonObjectFormat.ID, new JsonObjectParser());
    allParsers.put(JsonArrayFormat.ID, new JsonArrayParser());
    allParsers.put(CsvFormat.ID, new CsvParser());
    allParsers.put(GeoJsonFormat.ID, new GeoJsonParser());
    allParsers.put(XmlFormat.ID, new XmlParser());
    allParsers.put(ImageFormat.ID, new ImageParser());

    return allParsers;
  }
}

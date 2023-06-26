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

public class FormatIds {
  public static final String JSON = "Json";
  public static final String IMAGE = "Image";
  public static final String XML = "XML";
  public static final String CSV = "CSV";
  public static final String JSON_OBJECT_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/json/object";
  public static final String JSON_OBJECT_NEW_KEY = "object";
  public static final String JSON_ARRAY_KEY_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/json/arraykey";
  public static final String JSON_ARRAY_KEY_NEW_KEY = "arrayField";
  public static final String JSON_ARRAY_NO_KEY_FORMAT_ID =
      "https://streampipes.org/vocabulary/v1/format/json/arraynokey";
  public static final String JSON_ARRAY_NO_KEY_NEW_KEY = "array";
  public static final String IMAGE_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/image";
  public static final String GEOJSON_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/geojson";
  public static final String GEOJSON_NEW_KEY = "geojson";
  public static final String CSV_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/csv";
  public static final String XML_FORMAT_ID = "https://streampipes.org/vocabulary/v1/format/xml";
}

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

package org.apache.streampipes.export.utils;

import org.apache.streampipes.model.connect.adapter.migration.GenericAdapterConverter;
import org.apache.streampipes.model.connect.adapter.migration.IAdapterConverter;
import org.apache.streampipes.model.connect.adapter.migration.SpecificAdapterConverter;
import org.apache.streampipes.model.connect.adapter.migration.utils.AdapterModels;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.apache.streampipes.model.connect.adapter.migration.utils.AdapterModels.GENERIC_STREAM;

public class ImportAdapterMigrationUtils {

  public static String checkAndPerformMigration(String document) {
    JsonObject doc = JsonParser.parseString(document).getAsJsonObject();
    var docType = doc.get("@class").getAsString();
    if (AdapterModels.shouldMigrate(docType)) {
      if (AdapterModels.isSetAdapter(docType)) {
        throw new IllegalArgumentException("Sets are no longer supported");
      } else {
        var converter = getAdapterConverter(docType);
        return converter.convert(doc).toString();
      }
    } else {
      return doc.toString();
    }
  }

  private static IAdapterConverter getAdapterConverter(String adapterType) {
    if (adapterType.equals(GENERIC_STREAM)) {
      return new GenericAdapterConverter(true);
    } else {
      return new SpecificAdapterConverter(true);
    }
  }
}

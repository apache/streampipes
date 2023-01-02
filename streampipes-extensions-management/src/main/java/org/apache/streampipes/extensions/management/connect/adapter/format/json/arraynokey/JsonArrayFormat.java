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

package org.apache.streampipes.extensions.management.connect.adapter.format.json.arraynokey;


import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.AbstractJsonFormat;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.sdk.builder.adapter.FormatDescriptionBuilder;

public class JsonArrayFormat extends AbstractJsonFormat {

  public static final String ID = "https://streampipes.org/vocabulary/v1/format/json/arraynokey";

  @Override
  public IFormat getInstance(FormatDescription formatDescription) {
    return new JsonArrayFormat();
  }

  @Override
  public FormatDescription declareModel() {
    return FormatDescriptionBuilder.create(ID, "Array",
            "Each event consists of only one array of json objects, e.g. [{'value': 1}, {'value': 2}]")
        .addFormatType(JSON_FORMAT_TYPE)
        .build();
  }

  @Override
  public String getId() {
    return ID;
  }


}

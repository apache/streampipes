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

package org.apache.streampipes.storage.couchdb.serializer;

import org.apache.streampipes.model.client.ontology.EnumeratedRange;
import org.apache.streampipes.model.client.ontology.PrimitiveRange;
import org.apache.streampipes.model.client.ontology.QuantitativeValueRange;
import org.apache.streampipes.model.client.ontology.Range;
import org.apache.streampipes.model.client.ontology.RangeType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RangeSerializer implements JsonSerializer<Range>, JsonDeserializer<Range> {

  public JsonElement serialize(Range src, Type typeOfSrc, JsonSerializationContext context) {

    return context.serialize(src, src.getClass());

  }

  public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();
    String rangeType = jsonObject.get("rangeType").getAsString();

    RangeType rt = RangeType.valueOf(rangeType);
    if (rt == RangeType.ENUMERATION) {
      return context.deserialize(jsonObject, EnumeratedRange.class);
    } else if (rt == RangeType.QUANTITATIVE_VALUE) {
      return context.deserialize(jsonObject, QuantitativeValueRange.class);
    } else {
      return context.deserialize(jsonObject, PrimitiveRange.class);
    }
  }

}


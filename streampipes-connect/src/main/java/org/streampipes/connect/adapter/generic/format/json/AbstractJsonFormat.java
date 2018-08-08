/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.connect.adapter.generic.format.json;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.schema.EventSchema;

import java.util.Map;

public abstract class AbstractJsonFormat extends Format {

  @Override
  public Map<String, Object> parse(byte[] object) {
    EventSchema resultSchema = new EventSchema();

    JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();

    Map<String, Object> result = null;

    try {
      result = jsonDefinition.toMap(object);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }

    return  result;
  }

}

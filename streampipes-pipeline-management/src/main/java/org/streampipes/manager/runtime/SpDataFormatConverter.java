/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.manager.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;

import java.util.Map;

public class SpDataFormatConverter {

  private SpDataFormatDefinition spDataFormatDefinition;
  private JsonDataFormatDefinition jsonDataFormatDefinition;

  public SpDataFormatConverter(SpDataFormatDefinition spDataFormatDefinition) {
    this.spDataFormatDefinition = spDataFormatDefinition;
    this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
  }

  public String convert(byte[] message) throws SpRuntimeException {
    Map<String, Object> event = spDataFormatDefinition.toMap(message);
    return toJson(event);
  }

  private String toJson(Map<String, Object> message) throws SpRuntimeException {
    return new String(jsonDataFormatDefinition.fromMap(message));
  }

}

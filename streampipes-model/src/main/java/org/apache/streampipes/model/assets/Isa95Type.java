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

package org.apache.streampipes.model.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Isa95Type {
  PROCESS_CELL("PROCESS_CELL"),
  PRODUCTION_UNIT("PRODUCTION_UNIT"),
  PRODUCTION_LINE("PRODUCTION_LINE"),
  STORAGE_ZONE("STORAGE_ZONE"),
  UNIT("UNIT"),
  WORK_CELL("WORK_CELL"),
  STORAGE_UNIT("STORAGE_UNIT"),
  OTHER("OTHER");

  private final String value;

  Isa95Type(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static Isa95Type fromValue(String value) {
    for (Isa95Type type : Isa95Type.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown enum type " + value);
  }
}

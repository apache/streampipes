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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.model.runtime.field.PrimitiveField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PropertyDuplicateFilter {

  private final boolean ignoreDuplicates;
  private final Map<String, Object> lastValues;

  public PropertyDuplicateFilter(boolean ignoreDuplicates) {
    this.ignoreDuplicates = ignoreDuplicates;
    this.lastValues = new HashMap<>();
  }

  public boolean shouldIgnoreField(String sanitizedRuntimeName,
                                   PrimitiveField eventPropertyPrimitiveField) {
    var rawValue = eventPropertyPrimitiveField.getRawValue();
    boolean shouldIgnore = ignoreDuplicates && lastValues.containsKey(sanitizedRuntimeName)
        && Objects.equals(lastValues.get(sanitizedRuntimeName), rawValue);
    lastValues.put(sanitizedRuntimeName, rawValue);
    return shouldIgnore;
  }
}

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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaValueNormalizationUtils;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpcUaAlarmValueNormalizer {

  Object normalize(Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof DataValue dataValue) {
      return dataValueToMap(dataValue);
    }

    if (value instanceof XmlElement xmlElement) {
      return xmlElement.getFragment();
    }

    return OpcUaValueNormalizationUtils.tryNormalizeCommonValue(value, this::normalize)
        .orElseGet(() -> String.valueOf(value));
  }

  private Map<String, Object> dataValueToMap(DataValue dataValue) {
    var normalized = new LinkedHashMap<String, Object>();
    normalized.put("value", dataValue.getValue() != null ? normalize(dataValue.getValue().getValue()) : null);
    normalized.put("statusCode", dataValue.getStatusCode() != null ? dataValue.getStatusCode().getValue() : null);
    normalized.put("sourceTimestamp", dataValue.getSourceTime() != null ? dataValue.getSourceTime().getJavaTime() : null);
    normalized.put("serverTimestamp", dataValue.getServerTime() != null ? dataValue.getServerTime().getJavaTime() : null);
    return normalized;
  }
}

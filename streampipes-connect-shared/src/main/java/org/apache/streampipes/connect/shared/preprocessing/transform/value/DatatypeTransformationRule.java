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

package org.apache.streampipes.connect.shared.preprocessing.transform.value;


import org.apache.streampipes.connect.shared.DatatypeUtils;
import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import java.util.Map;

public class DatatypeTransformationRule implements TransformationRule {

  private final String eventKey;
  private String targetDatatypeXsd;

  public DatatypeTransformationRule(String eventKey,
                                    String originalDatatypeXsd,
                                    String targetDatatypeXsd) {
    this.eventKey = eventKey;
    this.targetDatatypeXsd = targetDatatypeXsd;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> event) {
    Object value = event.get(eventKey);
    Object transformedValue = transformDatatype(value);
    event.put(eventKey, transformedValue);
    return event;
  }

  public Object transformDatatype(Object value) {
    return DatatypeUtils.convertValue(value, targetDatatypeXsd);
  }
}

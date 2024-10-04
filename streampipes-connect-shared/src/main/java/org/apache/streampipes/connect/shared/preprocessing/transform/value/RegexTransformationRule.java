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

import org.apache.streampipes.connect.shared.preprocessing.SupportsNestedTransformationRule;

import java.util.List;
import java.util.Map;

public class RegexTransformationRule extends SupportsNestedTransformationRule {

  private final List<String> eventKeys;
  private final String regex;
  private final String replaceWith;
  private final boolean replaceAll;

  public RegexTransformationRule(
      List<String> eventKeys,
      String regex,
      String replaceWith,
      boolean replaceAll
  ) {
    this.eventKeys = eventKeys;
    this.regex = regex;
    this.replaceWith = replaceWith;
    this.replaceAll = replaceAll;
  }

  @Override
  protected List<String> getEventKeys() {
    return eventKeys;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKey) {
    var oldValue = event.get(eventKey.get(0));

    var newValue = "";

    if (oldValue instanceof String oldStringValue) {

      if (this.replaceAll) {
        newValue = oldStringValue.replaceAll(regex, this.replaceWith);
      } else {
        newValue = oldStringValue.replaceFirst(regex, this.replaceWith);
      }

      event.put(eventKey.get(0), newValue);
    } else {
      // add empty string if key is not present or wrong data type
      event.put(eventKey.get(0), newValue);
    }
  }
}

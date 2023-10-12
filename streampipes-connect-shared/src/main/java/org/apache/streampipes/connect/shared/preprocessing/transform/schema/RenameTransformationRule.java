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

package org.apache.streampipes.connect.shared.preprocessing.transform.schema;

import org.apache.streampipes.connect.shared.preprocessing.SupportsNestedTransformationRule;

import java.util.List;
import java.util.Map;

public class RenameTransformationRule extends SupportsNestedTransformationRule {
  private final List<String> oldKey;
  private final String newKey;

  public RenameTransformationRule(List<String> oldKey, String newKey) {
    this.oldKey = oldKey;
    this.newKey = newKey;
  }

  @Override
  protected List<String> getEventKeys() {
    return oldKey;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKeys) {
    Object o = event.get(eventKeys.get(0));
    event.remove(eventKeys.get(0));
    event.put(newKey, o);
  }
}


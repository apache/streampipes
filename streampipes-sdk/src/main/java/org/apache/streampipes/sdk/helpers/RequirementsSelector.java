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
package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.constants.PropertySelectorConstants;

public enum RequirementsSelector {

  FIRST_INPUT_STREAM(PropertySelectorConstants.FIRST_REQUIREMENT_PREFIX),
  SECOND_INPUT_STREAM(PropertySelectorConstants.SECOND_REQUIREMENT_PREFIX);

  private String requirementPrefix;

  RequirementsSelector(String prefix) {
    this.requirementPrefix = prefix;
  }

  public String toSelector(String internalId) {
    return requirementPrefix + PropertySelectorConstants.PROPERTY_DELIMITER + internalId;
  }
}

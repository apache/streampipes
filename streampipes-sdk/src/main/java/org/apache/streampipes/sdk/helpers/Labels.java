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

public class Labels {

  /**
   * Creates a new label with internalId, label and description. Fully-configured labels are required by static
   * properties and are mandatory for event properties.
   *
   * @param internalId  The internal identifier of the element, e.g., "latitude-field-mapping"
   * @param label       A human-readable title
   * @param description A human-readable brief summary of the element.
   * @return label object with the specified properties
   */
  public static Label from(String internalId, String label, String description) {
    return new Label(internalId, label, description);
  }

  /**
   * Creates a new label only with an internal id.
   * Static properties require a fully-specified label, see {@link #from(String, String, String)}
   *
   *
   * @param internalId The internal identifier of the element, e.g., "latitude-field-mapping"
   * @return label object with the internal id
   */
  public static Label withId(String internalId) {
    return new Label(internalId, "", "");
  }

  /**
   * Creates a label with the string value of an enum.
   * Static properties require a fully-specified label, see {@link #from(String, String, String)}
   * @param internalId The internal identifier of the element, e.g., "LATITUDE-FIELD-MAPPING"
   * @return label object with the specified properties
   */
  public static Label withId(Enum<?> internalId) {
    return new Label(internalId.name(), "", "");
  }

  public static Label empty() {
    return new Label("", "", "");
  }

}

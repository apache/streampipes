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

package org.apache.streampipes.extensions.connectors.camel.kamelet.model;

import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.List;

public record KameletPropertyDefinition(String name,
                                        String label,
                                        String description,
                                        PropertyInputType inputType,
                                        Datatypes datatype,
                                        boolean required,
                                        List<String> allowedValues,
                                        String defaultValue) {

  public String staticPropertyInternalName(String templateName) {
    return "camel-kamelet-param-" + sanitize(templateName) + "-" + sanitize(name);
  }

  public String displayLabel() {
    return label == null || label.isBlank() ? name : label;
  }

  private String sanitize(String value) {
    return value == null ? "" : value.replaceAll("[^a-zA-Z0-9]+", "-").replaceAll("(^-|-$)", "").toLowerCase();
  }

  public enum PropertyInputType {
    TEXT,
    SECRET,
    ONE_OF
  }
}
